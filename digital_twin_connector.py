from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from azure.digitaltwins.core import DigitalTwinsClient
from azure.identity import DefaultAzureCredential
from azure.core.exceptions import HttpResponseError
import uvicorn
import logging
from typing import Dict, Any
import os

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

app = FastAPI(title="Cow Digital Twin Connector",
             description="API for interacting with cow digital twins",
             version="1.0.0")

# CORS Configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Azure ADT Setup
def initialize_adt_client():
    try:
        endpoint = os.getenv("ADT_ENDPOINT", "https://Cow-DigitalTwinModel.api.wcus.digitaltwins.azure.net")
        credential = DefaultAzureCredential()
        return DigitalTwinsClient(endpoint, credential)
    except Exception as e:
        logger.error(f"Failed to initialize ADT client: {str(e)}")
        raise

client = initialize_adt_client()

@app.get("/cow/{cow_id}", response_model=Dict[str, Any],
         summary="Get cow digital twin details",
         responses={
             200: {"description": "Successfully retrieved cow data"},
             404: {"description": "Cow not found"},
             500: {"description": "Internal server error"}
         })
async def get_cow_details(cow_id: str):
    """
    Retrieve digital twin data for a specific cow
    
    - **cow_id**: Unique identifier of the cow (e.g. co-01)
    """
    try:
        logger.info(f"Fetching digital twin for cow: {cow_id}")
        twin = client.get_digital_twin(cow_id)
        
        if not twin:
            logger.warning(f"Cow {cow_id} not found")
            raise HTTPException(status_code=404, detail="Cow not found")
            
        logger.info(f"Successfully retrieved data for cow {cow_id}")
        return {"cows": [twin]}
        
    except HttpResponseError as e:
        logger.error(f"Azure ADT error for cow {cow_id}: {str(e)}")
        if e.status_code == 404:
            raise HTTPException(status_code=404, detail="Cow not found")
        raise HTTPException(status_code=500, detail=str(e))
    except Exception as e:
        logger.error(f"Unexpected error for cow {cow_id}: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")

@app.get("/health", include_in_schema=False)
async def health_check():
    """Endpoint for health checks"""
    return {"status": "healthy"}

if __name__ == "__main__":
    # Configuration for local development
    config = {
        "host": os.getenv("HOST", "0.0.0.0"),
        "port": int(os.getenv("PORT", 8000)),
        "reload": os.getenv("RELOAD", "true").lower() == "true",
        "workers": int(os.getenv("WORKERS", 1))
    }
    
    logger.info(f"Starting server on {config['host']}:{config['port']}")
    uvicorn.run(
        "digital_twin_connector:app",
        host=config["host"],
        port=config["port"],
        reload=config["reload"],
        workers=config["workers"]
    )