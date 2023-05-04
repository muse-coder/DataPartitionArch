package CGRA.PeStructure

import chisel3._
import chisel3.util._

class RegFile[T<:Data] (dType:T,depth:Int) extends Module{
  val io = IO(new Bundle() {
    val readAddr = Input(UInt(log2Ceil(depth).W))
    val writeAddr = Input(UInt(log2Ceil(depth).W))
    val readout = Output(dType)
    val writeIn = Input(dType)
  })
  val localRegFile = RegInit(VecInit(Seq.fill(depth)(0.U(dType.getWidth.W))))

  localRegFile(io.writeAddr) := io.writeIn
  io.readout := localRegFile(io.readAddr)

}
