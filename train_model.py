import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder
import pickle

# Load dataset
df = pd.read_csv("cow_health_dataset.csv")

# Drop unused columns and set target
df = df.drop(columns=['cow_id', 'health_status', 'estrus_status'])
X = df.drop(columns=['label'])
y = df['label']

# Categorical and numerical columns
categorical_cols = ['breed', 'estrus_sign']
numerical_cols = [col for col in X.columns if col not in categorical_cols]

# Preprocessing pipeline
preprocessor = ColumnTransformer([
    ('num', StandardScaler(), numerical_cols),
    ('cat', OneHotEncoder(handle_unknown='ignore'), categorical_cols)
])

# Model pipeline
pipeline = Pipeline([
    ('preprocessor', preprocessor),
    ('classifier', RandomForestClassifier(random_state=42))
])

# Split dataset
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train model
pipeline.fit(X_train, y_train)

# Evaluate model
y_pred = pipeline.predict(X_test)
print(classification_report(y_test, y_pred))

# Save model as .pkl
with open('cow_health_model.pkl', 'wb') as f:
    pickle.dump(pipeline, f)
