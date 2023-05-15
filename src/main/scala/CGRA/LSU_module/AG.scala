package CGRA.LSU_module
import CGRA.{BAGU, SAGU}
import chisel3._
import chisel3.util._
class AG (addrWidth:Int=8, bankNum:Int=8)extends Module {
  val io = IO(new Bundle() {
    val S1 = Input(UInt(addrWidth.W))
    val S2 = Input(UInt(addrWidth.W))
    val SA = Input(UInt(addrWidth.W))
    val maxj = Input(Bool())
    val N = Input(UInt(log2Ceil(bankNum).W))
    val log2_B =Input(UInt(log2Ceil(bankNum).W))
    val B = Input(UInt(log2Ceil(bankNum).W))
    val log2_N_B= Input(UInt(log2Ceil(bankNum*bankNum).W))
    val bankID = Output(UInt(log2Ceil(bankNum).W))
    val offset = Output(UInt(addrWidth.W))
  })
  val SAGU_u = Module(new SAGU(addrWidth = addrWidth))
  val BAGU_u = Module(new BAGU(addrWidth = addrWidth, bankNum = bankNum))

  SAGU_u.io.S1 := io.S1
  SAGU_u.io.S2 := io.S2
  SAGU_u.io.SA := io.SA
  SAGU_u.io.maxj := io.maxj

  BAGU_u.io.StreamAddress := SAGU_u.io.StreamAddress
  BAGU_u.io.N := io.N
  BAGU_u.io.log2_B := io.log2_B
  BAGU_u.io.B := io.B
  BAGU_u.io.log2_N_B := io.log2_N_B

  io.bankID := BAGU_u.io.bankID
  io.offset := BAGU_u.io.offset

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