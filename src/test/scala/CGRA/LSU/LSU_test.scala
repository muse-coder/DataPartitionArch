
import CGRA.LSU_module._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


class LSU_test extends AnyFlatSpec with ChiselScalatestTester {

  "IVG test" should "pass" in {

    test(new LSU (dType = UInt(32.W),addrWidth =10 ,LSU_InstWidth = 34)) { dut =>
      dut.io.LSU_config.poke("b010110101101011111".U)
      dut.io.IVG_x.poke(5.U)
      dut.io.IVG_x0.poke(5.U)
      dut.io.IVG_x1.poke(5.U)
      dut.io.dataFromPE.poke(5.U)
//      dut.io.dataToPE.poke(5.U)
      dut.io.dataFromBank.poke(5.U)
//      dut.io.dataToBank.poke(5.U)
//      dut.io.addresToBank.poke(5.U)
//      dut.io.bankID.poke(5.U)

      for (i<-0 until 400){
        dut.clock.step(1)
        println(s"LSU_configReg : ${dut.LSU_configReg.peek().toString()} }")
        println(s"STB_config : ${dut.STB.peek().toString()} }")
        println(s"N_config : ${dut.N.peek().toString()} }")
        println(s"log2_N_config : ${dut.log2_N.peek().toString()} }")
        println(s"d1_N_config : ${dut.d1_N.peek().toString()} }")
        println(s"store_sel_config : ${dut.store_sel.peek().toString()} }")

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