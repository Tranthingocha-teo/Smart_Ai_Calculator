# 🧠 Machine Learning: Handwritten Math Symbol Recognition (22 Classes)

This directory contains the complete training, evaluation, and export pipeline for the on-device mathematical symbol classifier deployed in the **Smart AI Calculator** Android application.

---

## 📌 Model Overview
- **Architecture**:
  - **CNN**: 4-layer Convolutional Neural Network with Batch Normalization and Dropout (< 350K params).
  - **MLP**: Lightweight 2-layer Neural Network (`784 -> 128 -> 22`) (< 415 KB).
- **Input**: Grayscale image `[1, 28, 28, 1]` normalized to `[0.0, 1.0]`, white stroke on black background.
- **Output**: Class probability distribution `[1, 22]` (Softmax).
- **Target Deployment**: TensorFlow Lite on Android (`math_symbol_model.tflite`), size `< 1.8 MB` (actual: ~404 KB).

### Supported 22 Mathematical Classes
```
Digits:     0, 1, 2, 3, 4, 5, 6, 7, 8, 9
Operators:  +, -, *, /, =
Symbols:    ., (, ), ,, ^
Variables:  x, y
```

---

## 🚀 How to Train & Export

### Option A: Local Training (Lightweight / CPU / CI)
No TensorFlow installation required. Uses Scikit-Learn and pure FlatBuffers:
```bash
# 1. Install dependencies
pip install -r requirements.txt

# 2. Run automated training & Android asset deployment
python train.py --samples 500 --epochs 50

# 3. Run pipeline tests
python test_pipeline.py
```

### Option B: Google Colab / Cloud GPU (TensorFlow Keras CNN)
1. Open `ml/train_math_ocr.ipynb` in [Google Colab](https://colab.research.google.com).
2. Set Runtime to **GPU (T4)**.
3. Run all cells. Training takes ~3 minutes.
4. Download the resulting `math_symbol_model.tflite` and `labels.txt` and place them into:
   `app/src/main/assets/models/`

---

## 📂 Directory Structure
- `dataset.py`: 22-class procedural data synthesis, MNIST loading, and spatial augmentation.
- `model.py`: Keras CNN model architecture definition.
- `tflite_builder.py`: Pure FlatBuffers builder for producing valid TFLite models.
- `train.py`: Unified training loop with automatic fallback (TensorFlow CNN or Scikit-Learn MLP) and asset deployment.
- `export_tflite.py`: Model quantization and export pipeline to Android assets.
- `test_pipeline.py`: Comprehensive test suite verifying dataset generation, model serialization, and asset integrity.
- `train_math_ocr.ipynb`: Interactive Jupyter Notebook for 1-click cloud execution.
- `labels.txt`: Class mapping for the 22 mathematical tokens.
- `schema.fbs`: Official TensorFlow Lite FlatBuffers schema.
