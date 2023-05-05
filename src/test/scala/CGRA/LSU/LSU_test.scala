
import CGRA.LSU_module.{LSU, _}
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import java.io._
import scala.math._
class LSU_test extends AnyFlatSpec with ChiselScalatestTester {


  def intToBin(num: Int, width: Int): String = {
    var binaryString = num.toBinaryString
    if (binaryString.length < width) {
      binaryString = "0" * (width - binaryString.length) + binaryString
    }
    binaryString
  }


  "IVG test" should "pass" in {
      val randomNum = scala.util.Random
    val outputFile = new File("./print.txt")
    //      val printStream = new PrintStream(new FileOutputStream(outputFile))
    //      val outputFile = new File(s"output_$i.txt")
    val printWriter = new PrintWriter(new FileWriter(outputFile))
    test(new LSU(dType = UInt(32.W) ,addrWidth =8,LSU_InstWidth=35,bankNum=8,countDepth=16)) { dut =>
//      val dataFromBank = randomNum.nextInt(100)+1
//      ************************init**********************
      dut.lsu_crossbar_io.dataFromBank.poke(randomNum.nextInt(100).U)
      dut.lsu_crossbar_io.dataValid.poke(true.B)
//      val randomNum = scala.util.Random
      val bi = (randomNum.nextInt(pow(2, 7).toInt), 8, "bi")
      val bj = (randomNum.nextInt(pow(2, 7).toInt), 8, "bj")
      val STB = (randomNum.nextInt(pow(2, 3).toInt), 3, "STB")
      val N = (randomNum.nextInt(pow(2, 3).toInt), 3, "N")
      val log2_N = (randomNum.nextInt(pow(2, 3).toInt), 3, "log2_N")
      val d1_N = (randomNum.nextInt(pow(2, 8).toInt), 8, "d1_N")
      var storeSel = (randomNum.nextInt(pow(2, 3).toInt) % 2, 1, "storeSel")
      var access = (randomNum.nextInt(pow(2, 5).toInt) % 2, 1, "access")
      var seq_u = Seq(bi, bj, STB, N, log2_N, d1_N, storeSel, access)
      var binarySeq = seq_u.map { case (a, b, c) => intToBin(a, b) }
      var concatSeq = binarySeq.reduce((a, b) => (b + a))
//      for (i <- 0 until seq_u.length) {
//            val tuple = seq_u(i)
//            val binaryValue = binarySeq(i)
//            println(s"name : ${tuple._3} value : $binaryValue")
//      }
      dut.config_io.bus.poke(BigInt(concatSeq, 2).U)
      dut.config_io.en.poke(1.U)
      dut.lsu_pe_io.dataFromPE.poke(randomNum.nextInt(100).U)
      dut.lsu_ivg_io.i.poke(randomNum.nextInt(12))
      dut.lsu_ivg_io.j.poke(randomNum.nextInt(12))
      dut.lsu_pe_io.readFifo.poke(false.B)

      //从bank中连续取16个数 存入FIFO  并且前10个cycle store LDR中的值 到bank  后6个cyclestore PE中的值
      var dataFromBank = 100
      for (i<-0 until 50){
        dut.lsu_pe_io.dataFromPE.poke(randomNum.nextInt(100).U)

        dut.lsu_crossbar_io.dataFromBank.poke((dataFromBank+i).U)
        if(i<10){
         storeSel = (1, 1, "storeSel")
          access = ( 0 , 1, "access")
          dut.config_io.en.poke(1.U)
        }
        else{
          storeSel = (0, 1, "storeSel")
          access = ( 1 , 1, "access")
        }
        if (i>20) {
          dut.lsu_pe_io.readFifo.poke(true.B)
          dut.lsu_crossbar_io.dataValid.poke(false.B)
          //
        }
        seq_u = Seq(bi, bj, STB, N, log2_N, d1_N, storeSel, access)
        binarySeq = seq_u.map { case (a, b, c) => intToBin(a, b) }
         concatSeq = binarySeq.reduce((a, b) => (b + a))
//        dut.lsu_crossbar_io.dataFromBank.poke(dataFromBank.U)
        dut.config_io.bus.poke(BigInt(concatSeq, 2).U)
//        dut.lsu_pe_io.dataFromPE.poke(randomNum.nextInt(100).U)

        println(s"LSU_configBus : ${(dut.config_io.bus.peek().litValue)} }")
        println(s"LSU_configEn : ${dut.config_io.en.peek().litValue} }")

        dut.clock.step(1)
        println(s"**************Clock  ${i} **************")
//        genConfig()

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