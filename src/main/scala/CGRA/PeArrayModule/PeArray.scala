package CGRA.PeArrayModule
import CGRA.PeStructure.Pe
import chisel3._
import chisel3.util._

class PeArray[T<: Data] (dType :T,column:Int=4,row:Int=4,instWidth:Int=13) extends  Module{
  val io= IO(new Bundle{
    val PErowConfig = Input(Vec(row,UInt(instWidth.W)))
    val config_sel = Input(Vec(row,UInt(2.W)))
    val row_left_in = Input(Vec(row,dType))
    val row_up_in = Input(Vec(column, dType))
    val row_left_out = Output(Vec(row,dType))
    val row_up_out = Output(Vec(column, dType))

  })
  val PeRows = Array.fill(row-1){Module (new PeRow(dType = dType,hasSouth = true,column = column,instWidth = instWidth))}

  val PeLastRow=Module (new PeRow(dType = dType,hasSouth = false,column = column,instWidth = instWidth))

  for (i <- 0 until row-1) {
    PeRows(i).io.PErowConfig := io.PErowConfig(i)
    PeRows(i).io.config_sel := io.config_sel(i)
    PeRows(i).io.row_left_in := io.row_left_in(i)
    io.row_left_out(i) := PeRows(i).io.row_left_out
    if(i ==0){
      PeRows(i).io.row_up_in := io.row_up_in
      io.row_up_out := PeRows(i).io.row_up_out
    }
    else{
      PeRows(i).io.row_up_in := PeRows(i-1).io.row_down_out.get
      PeRows(i-1).io.row_down_in.get := PeRows(i).io.row_up_out
    }


    if (i == row -2) {
      PeRows(i).io.row_down_in.get := PeLastRow.io.row_up_out
      PeLastRow.io.row_up_in := PeRows(i).io.row_down_out.get
    }
    else {
      PeRows(i).io.row_down_in.get := PeRows(i + 1).io.row_up_out
      PeRows(i + 1).io.row_up_in := PeRows(i).io.row_down_out.get
    }
  }
  PeLastRow.io.PErowConfig := io.PErowConfig(row-1)
  PeLastRow.io.config_sel := io.config_sel(row-1)
  PeLastRow.io.row_left_in := io.row_left_in(row-1)
  io.row_left_out(row-1) := PeLastRow.io.row_left_out


}

object PeArray_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new PeArray (dType =UInt(32.W),column=4,row=4,instWidth=13),
      Array(
        "--target-dir", "output/"+"PeArray"
      )
    )
  )
}