package CGRA.PeArrayModule
import CGRA.PeStructure.Pe
import chisel3._
import chisel3.util._

class PeRow[T<: Data] (dType :T,hasSouth:Boolean,column:Int=4,instWidth:Int=13) extends  Module{
  val io= IO(new Bundle{
    val PErowConfig = Input(UInt(instWidth.W))
    val config_sel = Input(UInt(2.W))
    val row_left_in = Input( dType)
    val row_up_in = Input(Vec(column, dType))
//    val row_down_in = if(hasSouth) (Input(Vec(column, dType))) else Input(Vec(column, None))
    val row_down_in = if (hasSouth) Some(Input(Vec(column, dType))) else None
    val row_left_out = Output( dType)
    val row_up_out = Output(Vec(column, dType))
    val row_down_out = if (hasSouth) Some(Output(Vec(column, dType))) else None

  })
  val PEs = Array.fill(column-1){Module (new Pe(dType = dType,hasSouth=hasSouth,hasEast = true ,instWidth=instWidth))}
  val PeRight=Module (new Pe(dType = dType,hasSouth=hasSouth,hasEast = false ,instWidth=13))

  for (i <- 0 until column-1) {
    when(io.config_sel === i.U) {
      PEs(i).io.configEnable := 1.U
    }.otherwise {
      PEs(i).io.configEnable := 0.U
    }
    PEs(i).io.configBus := io.PErowConfig

    PEs(i).io.dinBus.north := io.row_up_in(i)


    if (i == 0) {
      PEs(i).io.dinBus.west := io.row_left_in
      io.row_left_out := PEs(i).io.doutBus.west
    }
    else {
      PEs(i).io.dinBus.west := PEs(i - 1).io.doutBus.east.get
      PEs(i - 1).io.dinBus.east.get := PEs(i).io.doutBus.west
    }

    if (i == column - 2) {
      PEs(i).io.dinBus.east.get := PeRight.io.doutBus.west
      PeRight.io.dinBus.west := PEs(i).io.doutBus.east.get
    }
    else {
      PEs(i).io.dinBus.east.get := PEs(i + 1).io.doutBus.west
      PEs(i + 1).io.dinBus.west := PEs(i).io.doutBus.east.get
    }

    io.row_up_out(i) := PEs(i).io.doutBus.north
    if(hasSouth){
      PEs(i).io.dinBus.south.get := io.row_down_in.get(i)
      io.row_down_out.get(i) := PEs(i).io.doutBus.south.get
    }
  }
  when (io.config_sel===3.U){
    PeRight.io.configEnable := 1.U
  }.otherwise{
    PeRight.io.configEnable := 0.U
  }
  PeRight.io.configBus :=io.PErowConfig
  if(hasSouth){
    PeRight.io.dinBus.south.get := io.row_down_in.get(column-1)
    io.row_down_out.get(column-1) := PeRight.io.doutBus.south.get
  }
  //  PeRight.io.dinBus.east := io.row_up_in(column - 1)
  PeRight.io.dinBus.north := io.row_up_in(column - 1)

  io.row_up_out(column-1) := PeRight.io.doutBus.north
}

object PeRow_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new PeRow(dType = UInt(32.W),hasSouth = true,column = 4,instWidth = 13),
      Array(
        "--target-dir", "output/"+"PeRow"
      )
    )
  )
}