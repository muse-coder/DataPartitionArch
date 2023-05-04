package CGRA
import CGRA.Crossbar.CrossbarTop
import CGRA.LSU_module.LSU
import CGRA.PeArrayModule.{PeArray, PeRow}
import chisel3._
import chisel3.util._
class TopModule [T<:Data] (dType:T,LSU_InstWidth:Int=40,row:Int=4,column:Int=4,addrWidth:Int=8, Pe_instWidth:Int=13,bankNum:Int=8)extends  Module {
  val io = IO(new Bundle {
    val external_data_in = Input(dType)
    val external_control = Input(UInt(11.W))
    val external_data_out = Output(dType)
    val lsu_config = Input(Vec(bankNum,UInt(LSU_InstWidth.W)))
    val PErowConfig = Input(Vec(row,UInt(Pe_instWidth.W)))
    val Pe_config_sel = Input(Vec(row,UInt(2.W)))
  })
//   val PeArray_u = Module(new PeArray (dType = dType,column=column, row =row , instWidth=Pe_instWidth) )
     val LSUs = Array.fill(bankNum){Module (new LSU (dType = dType ,addrWidth=addrWidth,LSU_InstWidth=LSU_InstWidth,bankNum= bankNum,countDepth=16))}
      for (i<-0 until bankNum){
        LSUs(i).io.LSU_config := io.lsu_config(i)
      }

     val CrossbarTop_u = Module(new CrossbarTop(dType = dType , addressType = UInt(addrWidth.W)))
    CrossbarTop_u.Address_io

     val DataMem_u =Module(new DataMem(dataWidth = 32,addrWidth = addrWidth))


     val PeArray_u =Module(new PeArray(dType = dType,column=column, row =row , instWidth=Pe_instWidth))
     PeArray_u.io.PErowConfig := io.PErowConfig
     PeArray_u.io.config_sel := io.Pe_config_sel
     for (i <-0 until bankNum){
       if(i<bankNum/2){
         PeArray_u.io.row_left_in := io.Pe_config_sel

       }

     }

}
