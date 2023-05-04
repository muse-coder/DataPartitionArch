package simple

import chisel3._
import chiseltest._
import chisel3.util._
import org.scalatest.flatspec.AnyFlatSpec
//
///**
// * Test the Alu design
// */
//
//class AluTester extends AnyFlatSpec with ChiselScalatestTester {
//
//  "AluTester test" should "pass" in {
//    test(new Alu) { dut =>
//
//      // This is exhaustive testing, which usually is not possible
//      for (a <- 0 to 15) {
//        for (b <- 0 to 15) {
//          for (op <- 0 to 3) {
//            val result =
//              op match {
//                case 0 => a + b
//                case 1 => a - b
//                case 2 => a | b
//                case 3 => a & b
//              }
//            val resMask = result & 0x0f
//
//            dut.io.fn.poke(op.U)
//            dut.io.a.poke(a.U)
//            dut.io.b.poke(b.U)
//            dut.clock.step(1)
//            dut.io.result.expect(resMask.U)
//          }
//        }
//      }
//    }
//  }
//}


import chisel3._
import chiseltest._

class MyModule extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val out = Output(UInt(8.W))
  })

  val reg = RegInit(0.U(8.W))

  when(io.in > 0.U) {
    reg := io.in
  }

  io.out := reg
}

class MyModuleSpec extends AnyFlatSpec with ChiselScalatestTester {
  "MyModule" should "pass" in {
    test(new MyModule) { c =>
      c.io.in.poke(42.U)
      c.clock.step(1)
      printf(s"reg = ${c.reg.peek().toString()}")
      c.io.out.expect(42.U)
    }
  }
}