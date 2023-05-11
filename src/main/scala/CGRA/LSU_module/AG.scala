package CGRA.LSU_module
import chisel3._
import chisel3.util._
class AG (addrWidth:Int=8,countDepth:Int=16, bankNum:Int=16)extends Module {
  val io = IO(new Bundle() {
    val j = Input(UInt((countDepth).W))
    val i = Input(UInt((countDepth).W))
    val bj = Input(SInt(addrWidth.W))
    val bi = Input(SInt(addrWidth.W))
//    val STB = Input(UInt(log2Ceil(bankNum).W))
    val N = Input(UInt(log2Ceil(bankNum).W))
    val log2_N =Input(UInt(log2Ceil(bankNum).W))
    val d1_N = Input(UInt(addrWidth.W))
    val offset = Output(UInt(addrWidth.W))
    val bankID = Output(UInt(log2Ceil(bankNum).W))
  })

  val x0 = (io.bi + io.i.asSInt).asUInt
  val x1 = (io.bj + io.j.asSInt).asUInt

  io.offset := ((x1 >> io.log2_N).asUInt  +  ( x0 * io.d1_N.asSInt).asUInt).asUInt
  io.bankID := ((x0 + x1).asUInt &  io.N).asUInt
//  Print()
  def Print(): Unit = {
    printf(p"bi : ${io.bi}\n")
    printf(p"bj : ${io.bj}\n")
    printf(p"N : ${io.N}\n")
    printf(p"d1_N : ${io.d1_N}\n")
    printf(p"log2_N : ${io.log2_N}\n")

    printf(p"i : ${io.i}\n")
    printf(p"j : ${io.j}\n")

    printf(p"x0 : ${x0}\n")
    printf(p"x1 : ${x1}\n")
    printf(p"offset:  ${io.offset}\n")
    printf(p"bank ID:  ${io.bankID}\n")
  }
}
