package CGRA.LSU_module
import CGRA.LsuAGIO
import chisel3._
import chisel3.util._
class AccCounter (depth:Int=16,addrWidth:Int)extends Module{
  val io = IO(new Bundle() {
    val inc = Input(UInt(log2Ceil(addrWidth).W))
    val rst = Input(Bool())
    val en =  Input(Bool())
    val res = Output(UInt(log2Ceil(depth).W))
  })

  //  reset := io.rst
  val cnt = RegInit(UInt(log2Ceil(depth).W),0.U)
  when(io.en === true.B){
    when(io.rst=== true.B){
      cnt := 0.U
    }.otherwise{
      cnt := cnt + io.inc
    }
  }
  io.res := cnt
}
class AG (addrWidth:Int=8,countDepth:Int=16, bankNum:Int=16)extends Module {
  val io = IO(new LsuAGIO(addrWidth = addrWidth , bankNum = bankNum))

  val AccCounter1 = Module(new AccCounter(depth = countDepth, addrWidth = addrWidth))
  AccCounter1.io.inc := io.stride1
  AccCounter1.io.en := io.en
  val x1 = io.start1 + AccCounter1.io.res
  val Acc1Max = Mux(x1 === io.max1 , true.B,false.B)
  AccCounter1.io.rst := Acc1Max

  val AccCounter0 = Module(new AccCounter(depth = countDepth, addrWidth = addrWidth))
  AccCounter0.io.inc := io.stride0
  AccCounter0.io.en := Acc1Max
  val x0 = io.start0 + AccCounter0.io.res
  val Acc0Max = Mux(x0 === io.max0 , true.B,false.B)
  AccCounter0.io.rst := Acc0Max



  io.offset := ((x1 >> io.log2_N).asUInt  +  ( x0 * io.d1_N)).asUInt
  io.bankID := ((x0 + x1).asUInt &  io.N).asUInt
//  Print()
  def Print(): Unit = {
    printf(p"counter0 result: : ${AccCounter0.io.res}\n")
    printf(p"counter1 result: : ${AccCounter1.io.res}\n")
    printf(p"N : ${io.N}\n")
    printf(p"d1_N : ${io.d1_N}\n")
    printf(p"log2_N : ${io.log2_N}\n")


    printf(p"x0 : ${x0}\n")
    printf(p"x1 : ${x1}\n")
    printf(p"offset:  ${io.offset}\n")
    printf(p"bank ID:  ${io.bankID}\n")
  }
}
object AccCounter extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new AccCounter (depth=16,addrWidth=8),
      Array(
        "--target-dir","output/AG"
      )
    )
  )
}
