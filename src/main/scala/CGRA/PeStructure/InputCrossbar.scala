package CGRA.PeStructure

import chisel3._

class MuxToFu [T <: Data ] (dType :T,hasSouth:Boolean=true,hasEast:Boolean=true , instWidth :Int =3)  extends Module{
  val io = IO (new Bundle {
    val din =  (new DinPort(dType,hasSouth=hasSouth,hasEast=hasEast))
    val muxInst = Input(UInt(instWidth.W))
    val operator = Output(dType)
  })
  assert(instWidth==3,"mux inst must be width of 3")

  when(io.muxInst === "b000".U) {
//    io.operator := io.din.east.getOrElse(0.U)
        io.operator := io.din.east.getOrElse((0.U))
  }.elsewhen(io.muxInst === "b001".U) {
    io.operator := io.din.west
  }.elsewhen(io.muxInst === "b010".U) {
//    io.operator := io.din.south.getOrElse(0.U)
    io.operator := io.din.south.getOrElse(0.U)
  }.elsewhen(io.muxInst === "b011".U) {
    io.operator := io.din.north
  }.otherwise {
    io.operator := io.din.localReg
  }

}

class InputCrossbar [T <: Data ] (dType :T,hasSouth:Boolean=true,hasEast:Boolean=true, instWidth :Int=6)  extends Module{
  val io = IO (new Bundle {
    val din =   new DinPort(dType,hasSouth=hasSouth,hasEast = hasEast)
    val muxInst = Input(UInt(instWidth.W))
    val doutA = Output(dType)
    val doutB = Output(dType)
  })
  val opInstA = Wire(UInt(3.W))
  val opInstB = Wire(UInt(3.W))
//TODO 指令待定
   opInstA := io.muxInst(2,0)
   opInstB := io.muxInst(5,3)

  val MuxToFuDinA = Module(new MuxToFu(dType,hasSouth,hasEast,3))
  MuxToFuDinA.io.din <> io.din
  MuxToFuDinA.io.muxInst := opInstA
  io.doutA := MuxToFuDinA.io.operator

  val MuxToFuDinB = Module(new MuxToFu(dType,hasSouth,hasEast,3))
  MuxToFuDinB.io.din <> io.din
  MuxToFuDinB.io.muxInst := opInstB
  io.doutB := MuxToFuDinB.io.operator


}
