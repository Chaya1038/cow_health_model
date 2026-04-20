import pickle
import pandas as pd

# Load the trained model
with open('cow_health_model.pkl', 'rb') as f:
    model = pickle.load(f)

# Create test samples (no health_status or estrus_status!)
test_data = pd.DataFrame([
    # Test Case 1: Healthy + Estrus
    {
        'breed': 'Normal Breed',
        'rumination_time': 283,      # Healthy range
        'avg_lameness': 1.2,         # Healthy range
        'estrus_sign': 'none',   # Indicates estrus
        'ax': 2.1, 'ay': 2.3, 'az': 1.9,
        'gx': 3.2, 'gy': 3.0, 'gz': 2.9,
        'step_count': 8            # Estrus range
    },
    # Test Case 2: Unhealthy + Not in Estrus
    {
        'breed': 'Cross Breed',
        'rumination_time': 200,      # Unhealthy range
        'avg_lameness': 3.8,         # Unhealthy range
        'estrus_sign': 'none',       # No estrus sign
        'ax': 0.2, 'ay': 0.1, 'az': -0.1,
        'gx': 0.0, 'gy': -0.2, 'gz': 0.1,
        'step_count': 6              # Normal range
    },
    # Test Case 3: Healthy + Not in Estrus
    {
        'breed': 'Normal Breed',
        'rumination_time': 450,
        'avg_lameness': 1.5,
        'estrus_sign': 'none',
        'ax': 0.5, 'ay': 0.6, 'az': 0.4,
        'gx': 0.3, 'gy': 0.4, 'gz': 0.3,
        'step_count': 9
    },
    # Test Case 4: Unhealthy + Estrus
    {
        'breed': 'Cross Breed',
        'rumination_time': 280,
        'avg_lameness': 4.5,
        'estrus_sign': 'standing_mount',
        'ax': 2.5, 'ay': 2.8, 'az': 2.2,
        'gx': 3.5, 'gy': 3.2, 'gz': 3.1,
        'step_count': 38
    }
])

# Predict
predictions = model.predict(test_data)

# Show results
for i, pred in enumerate(predictions):
    print(f"Test Case {i+1} Prediction: {pred}")
