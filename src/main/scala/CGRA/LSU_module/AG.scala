package CGRA.LSU_module
import chisel3._
import chisel3.util._
class AG (addrWidth:Int=8,countDepth:Int=16, bankNum:Int=8)extends Module {
  val io = IO(new Bundle() {
    val j = Input(UInt(log2Ceil(countDepth).W))
    val i = Input(UInt(log2Ceil(countDepth).W))
    val bj = Input(UInt(addrWidth.W))
    val bi = Input(UInt(addrWidth.W))
    val STB = Input(UInt(log2Ceil(bankNum).W))
    val N = Input(UInt(log2Ceil(bankNum).W))
    val log2_N =Input(UInt(log2Ceil(bankNum).W))
    val d1_N = Input(UInt(addrWidth.W))
    val offset = Output(UInt(addrWidth.W))
    val bankID = Output(UInt(log2Ceil(bankNum).W))
  })
  io.offset := (io.i + io.bi) * io.d1_N  + ( (io.j + io.bj )  >> io.log2_N)
  io.bankID := (io.j + io.i ) &  io.N  + io.STB

}
