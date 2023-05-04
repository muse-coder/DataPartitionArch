package CGRA.PeStructure

import chisel3._
import chisel3.util.MixedVec
import firrtl.ir.UIntType


class DinPort [T<:Data] (dType :T,hasSouth:Boolean,hasEast:Boolean) extends  Bundle{
  val west = Input(dType)
  val east = if (hasEast) Some(Input(dType)) else None

  val south = if (hasSouth) Some(Input(dType)) else None
  val north = Input(dType)
  val localReg = Input(dType)
}

class DataIO [T<:Data] (dType :T,hasSouth:Boolean,hasEast:Boolean) extends  Bundle{
  val west = Input(dType)
  val east = if (hasEast) Some(Input(dType)) else None
  val south = if (hasSouth) Some(Input(dType)) else None
  val north = Input(dType)
}


class Pe [T <: Data ] (dType :T,hasSouth:Boolean,hasEast:Boolean,instWidth:Int=13)  extends Module{
  val io = IO(new Bundle() {
    val dinBus = new DataIO(dType,hasSouth,hasEast)
    val doutBus = Flipped(new DataIO(dType,hasSouth,hasEast))
    val configBus  = Input(UInt(instWidth.W))
    val configEnable = Input(UInt(1.W))
  })
//  RegInit(UInt(instWidth.W),0.U)
  val config_reg =   RegInit(UInt(instWidth.W),0.U)

  val configVec = config_reg.asTypeOf(MixedVec(Seq(
//    UInt(1.W),//configuration enable 1 bit
    UInt(6.W),//crossbarInst 6 bit
    UInt(3.W),//Fu inst 3 bit
    UInt(2.W),// Regfile read 2 bit
    UInt(2.W),// Regfile write 2 bit
  )))
//  val configEnable = configVec(0)
  val crossbarInst = configVec(0)
  val FuInst = configVec(1)
  val readPoint = configVec(2)
  val writePoint = configVec(3)

  when(io.configEnable===1.U){
    config_reg := io.configBus
  }.otherwise{
    config_reg := config_reg
  }

  val inputCrossbar_u = Module(new InputCrossbar(dType = dType,hasSouth,hasEast,6))
  val Fu_u = Module(new Fu(dataWidth = dType.getWidth ,3 ))
  val localRegFile = Module(new RegFile(dType,4))
  localRegFile.io.readAddr := readPoint
  localRegFile.io.writeAddr := writePoint
  localRegFile.io.writeIn := Fu_u.io.result

  inputCrossbar_u.io.din.north := io.dinBus.north
  if(hasSouth){
    inputCrossbar_u.io.din.south.get := io.dinBus.south.get
  }
  inputCrossbar_u.io.din.west := io.dinBus.west
  if(hasEast){
    inputCrossbar_u.io.din.east.get := io.dinBus.east.get

  }
  inputCrossbar_u.io.din.localReg := localRegFile.io.readout
  inputCrossbar_u.io.muxInst :=crossbarInst
  Fu_u.io.opcode :=FuInst
  Fu_u.io.dinA := inputCrossbar_u.io.doutA
  Fu_u.io.dinB := inputCrossbar_u.io.doutB

  val outputReg = RegNext(Fu_u.io.result,0.U)

  io.doutBus.west := outputReg
  io.doutBus.north:= outputReg
  if(hasSouth){

    io.doutBus.south.get:= outputReg
  }
  if(hasEast){
    io.doutBus.east.get := outputReg

  }

}


object Pe_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new Pe(UInt(32.W), hasSouth=false,hasEast=false,13),
      Array(
        "--target-dir", "output/"+"Pe"
      )
    )
  )
}