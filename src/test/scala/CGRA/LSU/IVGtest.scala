package fifo

import CGRA.LSU_module.{Count, IVG}
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
import simple.Alu


class IVGtest extends AnyFlatSpec with ChiselScalatestTester {

  "IVG test" should "pass" in {

    test(new IVG (addrWidth =10 ,countDepth=32)) { dut =>
      dut.io.AS_i.poke(4.U)
      dut.io.AS_j.poke(1.U)
      dut.io.SA_i.poke(0.U)
      dut.io.SA_j.poke(0.U)
      dut.io.max_i.poke(20.U)
      dut.io.max_j.poke(7.U)
      for (i<-0 until 400){
        dut.clock.step(1)
        println(s"x0 : ${dut.io.x0.peek().toString()} , x1 : ${dut.io.x1.peek().toString()},x : ${dut.io.x.peek().toString()}")
      }

    }

    }
}




//class FifoSpec extends AnyFlatSpec with ChiselScalatestTester {
//  def testFn[T <: Fifo[_ <: Data]](dut: T) = {
//    // Default values for all signals
//    dut.io.enq.bits.asUInt.poke(0xab.U)
//    dut.io.enq.valid.poke(false.B)
//    dut.io.deq.ready.poke(false.B)
//    dut.clock.step()
//
//    // Write one value and expect it on the deq side
//    dut.io.enq.bits.asUInt.poke(0x123.U)
//    dut.io.enq.valid.poke(true.B)
//    dut.clock.step()
//    dut.io.enq.bits.asUInt.poke(0xab.U)
//    dut.io.enq.valid.poke(false.B)
//    dut.clock.step(12)
//    dut.io.enq.ready.expect(true.B)
//    dut.io.deq.valid.expect(true.B)
//    dut.io.deq.bits.asUInt.expect(0x123.U)
//    // Read it out
//    dut.io.deq.ready.poke(true.B)
//    dut.clock.step()
//    dut.io.deq.valid.expect(false.B)
//    dut.io.deq.ready.poke(false.B)
//    dut.clock.step()
//
//    // Fill the whole buffer
//    // FIFO depth available as dut.depth. Test hard-coded for now.
//    var cnt = 1
//    dut.io.enq.valid.poke(true.B)
//    for (_ <- 0 until 12) {
//      dut.io.enq.bits.asUInt.poke(cnt.U)
//      if (dut.io.enq.ready.peek.litToBoolean)
//        cnt += 1
//      dut.clock.step()
//    }
//    println(s"Wrote ${cnt-1} words")
//    dut.io.enq.ready.expect(false.B)
//    dut.io.deq.valid.expect(true.B)
//    dut.io.deq.bits.asUInt.expect(1.U)
//
//    // Now read it back
//    var expected = 1
//    dut.io.enq.valid.poke(false.B)
//    dut.io.deq.ready.poke(true.B)
//    for (_ <- 0 until 12) {
//      if (dut.io.deq.valid.peek.litToBoolean) {
//        dut.io.deq.bits.asUInt.expect(expected.U)
//        expected += 1
//      }
//      dut.clock.step()
//    }
//
//    // Now do a speed test
//    dut.io.enq.valid.poke(true.B)
//    dut.io.deq.ready.poke(true.B)
//    cnt = 0
//    for (i <- 0 until 100) {
//      dut.io.enq.bits.asUInt.poke(i.U)
//      if (dut.io.enq.ready.peek.litToBoolean)
//        cnt += 1
//      dut.clock.step()
//    }
//    val cycles = 100.0 / cnt
//    println(s"$cnt words in 100 clock cycles, $cycles clock cycles per word")
//    assert(cycles >= 0.99, "Cannot be faster than one clock cycle per word")
//  }
//
//  "BubbleFifo" should "pass" in {
//    test(new BubbleFifo(UInt(16.W), 4)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
//      testFn(dut)
//    }
//  }
//
//  "DoubleBufferFifo" should "pass" in {
//    test(new DoubleBufferFifo(UInt(16.W), 4)).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
//      testFn(dut)
//    }
//  }
//}