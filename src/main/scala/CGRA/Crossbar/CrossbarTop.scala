package CGRA.Crossbar

import CGRA.LSU_module.{LSU, LsuCrossbarIO}
import chisel3.util._
import chisel3._
class CrossbarTop [T <: Data ](addrWidth:Int,dataWidth:Int) extends Module {


  val crossbar_lsu_io = IO(Vec(8,Flipped(new LsuCrossbarIO(dType = UInt(dataWidth.W),addrWidth = addrWidth))))
  val bank_crossbar_io = IO(Vec(8,new BankCrossbarIO(dType = UInt(dataWidth.W))))

//  val selReg = Reg(Vec(8,UInt(3.W)))
  val selReg = SyncReadMem(8,UInt(3.W))
  val Crossbar_Submodule_1 = Module(new Crossbar_Submodule(width = dataWidth + addrWidth + 1 +1))

  for (i<-0 until 8){
    val inBus = crossbar_lsu_io(i)
    Crossbar_Submodule_1.io.in(i) := Cat(inBus.dataToCrossbar,inBus.addresToCrossbar,inBus.dataOutValid.asUInt,inBus.readOrWrite)
    Crossbar_Submodule_1.io.sel(i) := crossbar_lsu_io(i).bankID

    val outBusVec = Crossbar_Submodule_1.io.out(i).asTypeOf(MixedVec(Seq(
      UInt(1.W),
      UInt(1.W),
      UInt(addrWidth.W),
      UInt(dataWidth.W)
    )))
    bank_crossbar_io(i).dataToBank := outBusVec(3)
    bank_crossbar_io(i).addressToBank := outBusVec(2)
    bank_crossbar_io(i).dataOutValid := outBusVec(1).asBool
    bank_crossbar_io(i).mode := outBusVec(0).asBool
    val sel = Crossbar_Submodule_1.io.sel(i)
    selReg(sel) := i.U
  }

  val Crossbar_Submodule_2 = Module(new Crossbar_Submodule(width = 33))
  for (i <- 0 until 8) {
    Crossbar_Submodule_2.io.in(i) := Cat(bank_crossbar_io(i).dataFromBank,bank_crossbar_io(i).dataInValid.asUInt)
    Crossbar_Submodule_2.io.sel(i) := selReg(i)
    crossbar_lsu_io(i).dataFromCrossbar := Crossbar_Submodule_2.io.out(i)(32,1)
    crossbar_lsu_io(i).dataInValid := Crossbar_Submodule_2.io.out(i)(0,0).asBool
  }

}



//   crossbar
class BankCrossbarIO [T <: Data ](dType:T,addrWidth:Int= 8) extends Bundle {
  val dataFromBank = Input(dType)
  val dataInValid = Input(Bool())
  val addressToBank = Output(UInt(addrWidth.W))
  val dataToBank = Output(dType)

  val dataOutValid = Output(Bool())
  val mode = Output(Bool())
}


class Crossbar_Submodule(width:Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(8, UInt(width.W)))    // 8个32位输入
    val sel = Input(Vec(8, UInt(3.W)))    // 每个输出端口的3位选择信号
    val out = Output(Vec(8, UInt(width.W)))  // 8个32位输出
  })
  // 为每个输出端口定义一个选择电路
  for (i <- 0 until 8) {
    val sel_bits = io.sel(i)
    io.out(i) := io.in(sel_bits)
  }
}

object Crossbar_Top_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new CrossbarTop(addrWidth = 8,dataWidth = 32),
      Array(
        "--target-dir","output/Crossbar_Top_u"
      )
    )
  )
}

