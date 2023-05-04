package CGRA
import CGRA.Crossbar.CrossbarTop
import chisel3._
import chisel3.util._

import scala.math._
class DataMem [T<:Data] (dataWidth:Int,addrWidth:Int)extends Module {
  val io = IO(new Bundle {
    val data_from_crossbar = Input(Vec(8,UInt(dataWidth.W)))
    val write_address_from_crossbar = Input(Vec(8, UInt(addrWidth.W)))
    val read_address_from_crossbar = Input(Vec(8, UInt(addrWidth.W)))

    val external_data_in = Input(UInt(dataWidth.W))
    val external_control = Input(UInt(11.W))
    val data_to_crossbar = Output(Vec(8,UInt(dataWidth.W)))
    val external_data_out = Output(UInt(dataWidth.W))
  })
  val bank_select = Wire(Vec(8,UInt(1.W)))

  val external_output_sel = io.external_control(8+2,8)
  val data_to_bank = Wire(Vec(8,UInt(dataWidth.W)))

  val depth = pow(2, addrWidth).toInt
  val mems = List.fill(8)(SyncReadMem(depth, UInt(32.W)))


  for (i <- 0 until 8){
    bank_select(i) := io.external_control(i)
    when(bank_select(i)===1.U){
      data_to_bank(i) := io.data_from_crossbar(i)
    }.otherwise{
      data_to_bank(i) := io.external_data_in
    }
    mems(i).write(io.write_address_from_crossbar(i),data_to_bank(i))

    io.data_to_crossbar(i) := mems(i).read(io.read_address_from_crossbar(i))
  }

  io.external_data_out := MuxLookup(external_output_sel, 0.U, Array(
    0.U -> io.data_to_crossbar(0),
    1.U -> io.data_to_crossbar(1),
    2.U -> io.data_to_crossbar(2),
    3.U -> io.data_to_crossbar(3),
    4.U -> io.data_to_crossbar(4),
    5.U -> io.data_to_crossbar(5),
    6.U -> io.data_to_crossbar(6),
    7.U -> io.data_to_crossbar(7),
  ))

}



object DataMem_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new DataMem(dataWidth = 32,addrWidth = 10) ,
      Array(
        "--target-dir", "output/"+"DataMem"
      )
    )
  )
}