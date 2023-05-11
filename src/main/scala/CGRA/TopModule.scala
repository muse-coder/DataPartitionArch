package CGRA
import CGRA.Crossbar.CrossbarTop
import CGRA._
import CGRA.LSU_module.{IVG, LSU}
import CGRA.PeArrayModule.{PeArray, PeRow}
import chisel3._
import chisel3.util._
class DataPartition [T<:Data] (dataWidth:Int,LSU_InstWidth:Int=36,row:Int=4,column:Int=4,addrWidth:Int=8, PeInstWidth:Int=13,bankNum:Int=8)extends  Module {
  val external_io = IO(new ExternalIO(dataWidth = dataWidth,configWidth = 11))
  val IvgConfig_io = IO(new IvgConfigIO(countDepth = 16))
  val PErowConfig_io = IO(Input(Vec(row, UInt(PeInstWidth.W))))
  val PErowSel_io = IO(Input(Vec(row, UInt(2.W))))
  val LsuConfig_io = IO(Vec(bankNum,new LsuConfigIO(LSU_InstWidth = LSU_InstWidth)))

  val DataMem_u = Module(new DataMem(dataWidth = dataWidth,addrWidth = addrWidth))
  val Crossbar_u = Module(new CrossbarTop(addrWidth = addrWidth,dataWidth = dataWidth))
  val Ivg_u = Module(new IVG(countDepth = 16))
  val LSUs = Array.fill(8){Module(new LSU(dType = UInt(dataWidth.W) ,addrWidth =addrWidth,LSU_InstWidth=LSU_InstWidth,bankNum=bankNum,countDepth=16))}
  val PeArray_u = Module(new PeArray(dType = UInt(dataWidth.W), column = column,row = row,instWidth = PeInstWidth))

  DataMem_u.external_io<>external_io
  Ivg_u.config_io<>IvgConfig_io
  PeArray_u.io.PErowConfig<>PErowConfig_io
  for (i <-0 until 8){
    LsuConfig_io(i)<> LSUs(i).config_io
    Crossbar_u.crossbar_lsu_io(i) <> LSUs(i).lsu_crossbar_io
    DataMem_u.bankCrossbar_io(i) <> Crossbar_u.bank_crossbar_io(i)
    LSUs(i).lsu_ivg_io<>Ivg_u.ivg_lsu_io
    if(i<4){
      LSUs(i).lsu_pe_io.dataFromPE := PeArray_u.io.row_left_out(i)
      PeArray_u.io.row_left_in(i)  :=LSUs(i).lsu_pe_io.dataToPE
    }
    else {
      LSUs(i).lsu_pe_io.dataFromPE := PeArray_u.io.row_up_out(i-4)
      PeArray_u.io.row_up_in(i-4)  :=LSUs(i).lsu_pe_io.dataToPE

    }
  }
  for (i<-0 until row){
    PErowSel_io(i)<> PeArray_u.io.config_sel(i)
  }

}

object TopModule_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new DataPartition (dataWidth=32),
      Array(
        "--target-dir", "output/"+"DataPartition"
      )
    )
  )
}
