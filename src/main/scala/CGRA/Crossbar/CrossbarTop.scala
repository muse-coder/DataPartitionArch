package CGRA.Crossbar

import chisel3.util._
import chisel3._

class CrossbarTop [T <: Data ](dType:T,addressType:T) extends Module{

  val Data_io = IO(new CrossbarIO(dType = dType))
  val Data_Crossbar = Module(new DataCrossbar(dType=dType))

  Data_Crossbar.io <> Data_io
  val Address_io = IO(new CrossbarIO(dType = addressType))

  val Address_Crossbar = Module(new DataCrossbar(dType = addressType))
  Address_Crossbar.io <> Address_io

}



object CrossbarTop_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new CrossbarTop(dType = UInt(32.W),addressType = UInt(10.W)) ,
      Array(
        "--target-dir", "output/"+"Crossbar"
      )
    )
  )
}