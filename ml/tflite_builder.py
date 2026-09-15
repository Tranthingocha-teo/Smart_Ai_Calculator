"""
TFLite FlatBuffer Model Builder for Math Symbol Classifier.
Builds fully compliant .tflite files using FlatBuffers without requiring full TensorFlow.
"""

import os
import sys
import flatbuffers
import numpy as np

# Ensure ml directory is in sys.path for tflite imports
current_dir = os.path.dirname(os.path.abspath(__file__))
if current_dir not in sys.path:
    sys.path.insert(0, current_dir)

from tflite.Model import ModelT
from tflite.SubGraph import SubGraphT
from tflite.Tensor import TensorT
from tflite.Operator import OperatorT
from tflite.OperatorCode import OperatorCodeT
from tflite.Buffer import BufferT
from tflite.TensorType import TensorType
from tflite.BuiltinOperator import BuiltinOperator
from tflite.BuiltinOptions import BuiltinOptions
from tflite.FullyConnectedOptions import FullyConnectedOptionsT
from tflite.SoftmaxOptions import SoftmaxOptionsT
from tflite.ActivationFunctionType import ActivationFunctionType


def build_math_symbol_tflite(w1: np.ndarray, b1: np.ndarray, w2: np.ndarray, b2: np.ndarray, output_path: str):
    """
    Builds a 2-layer MLP classifier with Softmax output in valid TFLite format.
    
    Architecture:
      Input:   [1, 28, 28, 1] FLOAT32 (normalized grayscale image)
      Layer 1: FullyConnected [128, 784] + ReLU -> [1, 128]
      Layer 2: FullyConnected [22, 128]  + NONE -> [1, 22] (logits)
      Layer 3: Softmax (beta=1.0)               -> [1, 22] (probabilities)
    
    Args:
      w1: Shape (128, 784) float32
      b1: Shape (128,) float32
      w2: Shape (22, 128) float32
      b2: Shape (22,) float32
      output_path: File path to write .tflite model
    """
    w1 = np.ascontiguousarray(w1, dtype=np.float32)
    b1 = np.ascontiguousarray(b1, dtype=np.float32)
    w2 = np.ascontiguousarray(w2, dtype=np.float32)
    b2 = np.ascontiguousarray(b2, dtype=np.float32)

    model = ModelT()
    model.version = 3
    model.description = "Smart AI Math Symbol Classifier (22 Classes)"

    # Operator Codes:
    # 0 -> FULLY_CONNECTED
    # 1 -> SOFTMAX
    op_fc = OperatorCodeT()
    op_fc.deprecatedBuiltinCode = min(BuiltinOperator.FULLY_CONNECTED, 127)
    op_fc.builtinCode = BuiltinOperator.FULLY_CONNECTED
    op_fc.version = 1

    op_sm = OperatorCodeT()
    op_sm.deprecatedBuiltinCode = min(BuiltinOperator.SOFTMAX, 127)
    op_sm.builtinCode = BuiltinOperator.SOFTMAX
    op_sm.version = 1

    model.operatorCodes = [op_fc, op_sm]

    # Buffers:
    # Buffer 0: empty dummy buffer for runtime activation tensors
    b0 = BufferT()
    b0.data = None

    # Buffer 1: FC1 weights
    b_w1 = BufferT()
    b_w1.data = list(w1.tobytes())

    # Buffer 2: FC1 bias
    b_b1 = BufferT()
    b_b1.data = list(b1.tobytes())

    # Buffer 3: FC2 weights
    b_w2 = BufferT()
    b_w2.data = list(w2.tobytes())

    # Buffer 4: FC2 bias
    b_b2 = BufferT()
    b_b2.data = list(b2.tobytes())

    model.buffers = [b0, b_w1, b_b1, b_w2, b_b2]

    # SubGraph
    sg = SubGraphT()
    sg.name = "main"

    # Tensor 0: input [1, 28, 28, 1]
    t_in = TensorT()
    t_in.shape = [1, 28, 28, 1]
    t_in.type = TensorType.FLOAT32
    t_in.buffer = 0
    t_in.name = "input"

    # Tensor 1: FC1 weights [128, 784]
    t_w1 = TensorT()
    t_w1.shape = list(w1.shape)
    t_w1.type = TensorType.FLOAT32
    t_w1.buffer = 1
    t_w1.name = "fc1/weights"

    # Tensor 2: FC1 bias [128]
    t_b1 = TensorT()
    t_b1.shape = list(b1.shape)
    t_b1.type = TensorType.FLOAT32
    t_b1.buffer = 2
    t_b1.name = "fc1/bias"

    # Tensor 3: FC1 output [1, 128]
    t_h1 = TensorT()
    t_h1.shape = [1, 128]
    t_h1.type = TensorType.FLOAT32
    t_h1.buffer = 0
    t_h1.name = "fc1/output"

    # Tensor 4: FC2 weights [22, 128]
    t_w2 = TensorT()
    t_w2.shape = list(w2.shape)
    t_w2.type = TensorType.FLOAT32
    t_w2.buffer = 3
    t_w2.name = "fc2/weights"

    # Tensor 5: FC2 bias [22]
    t_b2 = TensorT()
    t_b2.shape = list(b2.shape)
    t_b2.type = TensorType.FLOAT32
    t_b2.buffer = 4
    t_b2.name = "fc2/bias"

    # Tensor 6: FC2 logits [1, 22]
    t_logits = TensorT()
    t_logits.shape = [1, 22]
    t_logits.type = TensorType.FLOAT32
    t_logits.buffer = 0
    t_logits.name = "fc2/logits"

    # Tensor 7: Output probabilities [1, 22]
    t_out = TensorT()
    t_out.shape = [1, 22]
    t_out.type = TensorType.FLOAT32
    t_out.buffer = 0
    t_out.name = "output"

    sg.tensors = [t_in, t_w1, t_b1, t_h1, t_w2, t_b2, t_logits, t_out]
    sg.inputs = [0]
    sg.outputs = [7]

    # Operator 0: FC1 (in -> h1) with RELU activation
    op0 = OperatorT()
    op0.opcodeIndex = 0
    op0.inputs = [0, 1, 2]
    op0.outputs = [3]
    fc0_opt = FullyConnectedOptionsT()
    fc0_opt.fusedActivationFunction = ActivationFunctionType.RELU
    op0.builtinOptions = fc0_opt
    op0.builtinOptionsType = BuiltinOptions.FullyConnectedOptions

    # Operator 1: FC2 (h1 -> logits) with NONE activation
    op1 = OperatorT()
    op1.opcodeIndex = 0
    op1.inputs = [3, 4, 5]
    op1.outputs = [6]
    fc1_opt = FullyConnectedOptionsT()
    fc1_opt.fusedActivationFunction = ActivationFunctionType.NONE
    op1.builtinOptions = fc1_opt
    op1.builtinOptionsType = BuiltinOptions.FullyConnectedOptions

    # Operator 2: Softmax (logits -> output)
    op2 = OperatorT()
    op2.opcodeIndex = 1
    op2.inputs = [6]
    op2.outputs = [7]
    sm_opt = SoftmaxOptionsT()
    sm_opt.beta = 1.0
    op2.builtinOptions = sm_opt
    op2.builtinOptionsType = BuiltinOptions.SoftmaxOptions

    sg.operators = [op0, op1, op2]
    model.subgraphs = [sg]

    # Serialize FlatBuffer
    builder = flatbuffers.Builder(1024 * 1024)
    offset = model.Pack(builder)
    builder.Finish(offset, file_identifier=b"TFL3")

    model_bytes = builder.Output()
    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    with open(output_path, "wb") as f:
        f.write(model_bytes)

    size_kb = len(model_bytes) / 1024.0
    print(f"Generated TFLite model: {output_path} ({size_kb:.2f} KB, magic: {model_bytes[4:8]})")
    return output_path
