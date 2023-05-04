/*
 *
 * An ALU is a minimal start for a processor.
 *
 */

package CGRA.PeStructure

import chisel3._

/**
 * This is a very basic ALU example.
 */
class Fu [T <:Data] (dataWidth:Int=32 ,FuInstWidth:Int=3) extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(FuInstWidth.W))
    val dinA = Input(UInt(dataWidth.W))
    val dinB = Input(UInt(dataWidth.W))
    val result = Output(UInt(dataWidth.W))
  })

  // Use shorter variable names
  val opcode = io.opcode

  when(opcode === 0.U) {
    io.result := io.dinA + io.dinB
  }.elsewhen(opcode === 1.U) {
    io.result := io.dinA - io.dinB
  }.elsewhen(opcode === 2.U) {
    io.result := io.dinA & io.dinB
  }.elsewhen(opcode === 3.U) {
    io.result := io.dinA | io.dinB
  }.elsewhen(opcode === 4.U) {
    io.result := ~io.dinA
  }.elsewhen(opcode === 5.U) {
    io.result := io.dinA * io.dinB
  }.otherwise {
    io.result := 0.U
  }

}

/**
// * A top level to wire FPGA buttons and LEDs
// * to the ALU input and output.
// */
//class AluTop extends Module {
//  val io = IO(new Bundle {
//    val sw = Input(UInt(10.W))
//    val led = Output(UInt(10.W))
//  })
//
//  val alu = Module(new Alu())
//
//  // Map switches to the ALU input ports
//  alu.io.fn := io.sw(1, 0)
//  alu.io.a := io.sw(5, 2)
//  alu.io.b := io.sw(9, 6)
//
//  // And the result to the LEDs (with 0 extension)
//  io.led := alu.io.result
//}
//
//// Generate the Verilog code
//object AluMain extends App {
//  println("Generating the ALU hardware")
//  (new chisel3.stage.ChiselStage).emitVerilog(new AluTop(), Array("--target-dir", "generated"))
//
//}
//
