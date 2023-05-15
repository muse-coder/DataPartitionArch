package CGRA.LSU_module
import CGRA.{BAGU, SAGU}
import chisel3._
import chisel3.util._
class AG (addrWidth:Int=8, bankNum:Int=8)extends Module {
  val io = IO(new Bundle() {
    val i = Input(UInt(addrWidth.W))
    val j = Input(UInt(addrWidth.W))
    val bi = Input(UInt(addrWidth.W))
    val bj = Input(UInt(addrWidth.W))
    val B = Input(UInt(addrWidth.W))
    val d1_N_B_B = Input(UInt(addrWidth.W))
    val log2_B = Input(UInt(addrWidth.W))
    val log2_N_B = Input(UInt(addrWidth.W))
//    val N = Input(UInt(log2Ceil(bankNum).W))
    val alpha0 = Input(UInt(addrWidth.W))
    val alpha1 = Input(UInt(addrWidth.W))
    val bankID = Output(UInt(log2Ceil(bankNum).W))
    val offset = Output(UInt(addrWidth.W))
  })
    val x0 = io.bi + io.i
    val x1 = io.bj + io.j
    val alpha_x1 =  io.alpha1 * x1
    val alpha_x0 =  io.alpha0 * x0
    io.bankID := (alpha_x1 + alpha_x0) >> io.log2_B

    val offset0 = x0 * io.d1_N_B_B
    val offset1 = (x1 >> io.log2_N_B) << io.log2_B
    val offset2 = (x1 & io.B)
    io.offset := offset0 + offset1 + offset2
}

object AG_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new AG(addrWidth = 8,bankNum = 8) ,
      Array(
        "--target-dir", "output/"+"AG"
      )
    )
  )
}