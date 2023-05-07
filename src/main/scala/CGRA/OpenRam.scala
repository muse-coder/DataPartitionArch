package CGRA
import chisel3._
import chisel3.util._
class Ram_256_words extends BlackBox{
  val io = IO(new Bundle {
    val clk0 = Input(Clock())
    val csb0 = Input(Bool())
    val addr0 = Input(UInt(8.W))
    val din0 = Input(UInt(32.W))
    val clk1 = Input(Clock())
    val csb1 = Input(Bool())
    val addr1 = Input(UInt(8.W))
    val dout1 = Output(UInt(32.W))

  })
}
