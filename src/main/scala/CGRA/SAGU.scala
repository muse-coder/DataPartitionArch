package CGRA
import chisel3._
import chisel3.util._

class SAGU (addrWidth:Int ,countDepth:Int)extends Module {
  val io = IO(new SaguIO(addrWidth = addrWidth,countDepth = countDepth))
  val inc = Mux(io.maxj,io.S1,io.S2)
  val AccAddress = RegInit(UInt(addrWidth.W),0.U)
  AccAddress := AccAddress + inc
  io.StreamAddress := io.SA + AccAddress
}
class BAGU (addrWidth:Int ,bankNum:Int)extends Module {
  val io = IO(new BaguIO(addrWidth = addrWidth,bankNum=bankNum))
  val a = io.StreamAddress >> io.log2_B

  io.bankID := (a & io.N.asTypeOf(a))
  val b = io.StreamAddress & io.B
  val c = io.StreamAddress >> io.log2_N_B
  val d = c << io.log2_B
  io.offset := (b + d)

}
object BAGU_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new BAGU(addrWidth = 8,bankNum = 8) ,
      Array(
        "--target-dir", "output/"+"BAGU"
      )
    )
  )
}