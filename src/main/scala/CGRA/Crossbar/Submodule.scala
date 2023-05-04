package CGRA.Crossbar

import chisel3.util._
import chisel3._
class CrossbarIO [T <: Data ](dType:T) extends Bundle{
  val bank_store_sel = Input(Vec(8, UInt(3.W)))
  val bank_load_sel = Input(Vec(8, UInt(3.W)))
  val data_from_bank = Input(Vec(8, dType))
  val data_from_lsu = Input(Vec(8, dType))
  val data_to_bank = Output(Vec(8, dType))
  val data_to_lsu = Output(Vec(8, dType))
}

class DataCrossbar [T <: Data ](dType:T) extends Module{

  val io = IO(new CrossbarIO(dType = dType))

  val Load_Crossbar = Module(new Crossbar_Submodule(dType=dType))
  Load_Crossbar.io.sel := io.bank_load_sel
  Load_Crossbar.io.in := io.data_from_bank
  io.data_to_lsu := Load_Crossbar.io.out
  val Store_Crossbar = Module(new Crossbar_Submodule(dType=dType))
  Store_Crossbar.io.sel := io.bank_store_sel
  Store_Crossbar.io.in := io.data_from_lsu
  io.data_to_bank := Store_Crossbar.io.out
}


class Crossbar_Submodule[T<: Data](dType:T) extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(8, UInt(32.W)))    // 8个32位输入
    val sel = Input(Vec(8, UInt(3.W)))    // 每个输出端口的3位选择信号
    val out = Output(Vec(8, UInt(32.W)))  // 8个32位输出
  })

  // 为每个输出端口定义一个选择电路
  for (i <- 0 until 8) {
    val sel_bits = io.sel(i)
    // 根据选择信号的值，从8个输入端口中选择一个作为输出
    io.out(i) := MuxLookup(sel_bits, 0.U, Array(
      "b000".U -> io.in(0),
      "b001".U -> io.in(1),
      "b010".U -> io.in(2),
      "b011".U -> io.in(3),
      "b100".U -> io.in(4),
      "b101".U -> io.in(5),
      "b110".U -> io.in(6),
      "b111".U -> io.in(7)
    ))
  }
}

//object Crossbar_u extends App {
//  // These lines generate the Verilog output
//  println(
//    new (chisel3.stage.ChiselStage).emitVerilog(
//      new DataCrossbar(dType = UInt(32.W)) ,
//      Array(
//        "--target-dir", "output/"+"Crossbar"
//      )
//    )
//  )
//}