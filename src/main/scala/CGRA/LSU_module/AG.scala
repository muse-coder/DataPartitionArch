package CGRA.LSU_module
import CGRA.{BAGU, LsuAGIO, SAGU}
import chisel3._
import chisel3.util._
class AccCounter (addrWidth:Int)extends Module{
  val io = IO(new Bundle() {
    val inc = Input(UInt((addrWidth).W))
    val rst = Input(Bool())
    val en =  Input(Bool())
    val res = Output(UInt(addrWidth.W))
  })
  //  reset := io.rst
  val cnt = RegInit(UInt(addrWidth.W),0.U)
  when(io.en === true.B){
    when(io.rst=== true.B){
      cnt := 0.U
    }.otherwise{
      cnt := cnt + io.inc
    }
  }
  io.res := cnt
}

class AG (addrWidth:Int=8, bankNum:Int=8)extends Module {
  val io = IO(new LsuAGIO(addrWidth = addrWidth , bankNum = bankNum))
  val AccCounter1 = Module(new AccCounter(addrWidth = addrWidth))
  AccCounter1.io.inc := io.stride1
  AccCounter1.io.en := io.en
  val x1 = io.start1 + AccCounter1.io.res
  val Acc1Max = Mux(x1 === io.max1 , true.B,false.B)
  AccCounter1.io.rst := Acc1Max
  val AccCounter0 = Module(new AccCounter(addrWidth = addrWidth))
  AccCounter0.io.inc := io.stride0
  AccCounter0.io.en := Acc1Max
  val x0 = io.start0 + AccCounter0.io.res
  val Acc0Max = Mux(x0 === io.max0 , true.B,false.B)
  AccCounter0.io.rst := Acc0Max

    val alpha_x1 =  io.alpha1 * x1
    val alpha_x0 =  io.alpha0 * x0
    io.bankID := ((alpha_x1 + alpha_x0) >> io.log2_B) & io.N

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