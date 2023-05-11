package fifo

import CGRA.LSU_module._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import chiseltest.experimental._
import chiseltest.internal._

class AGtest extends AnyFlatSpec with ChiselScalatestTester {

  "AG test" should "pass" in {

    test(new AG (addrWidth=8,countDepth=16, bankNum=8 )) { dut =>
      val randomNum = scala.util.Random
      val d1 = 32
      val N = 7
      val bi_seq = Seq(1,-1,-1,0,0,0,1,1,1)
      val bj_seq = Seq(2, 1, 0, 2, 1, 0, 2, 1, 0)

      for (i<-0 until bi_seq.length){

        dut.io.i.poke(10)
        dut.io.j.poke(5)
        dut.io.bj.poke(bj_seq(i))
        dut.io.bi.poke(bi_seq(i))
        dut.io.N.poke(N)
        dut.io.log2_N.poke(3)
        dut.io.d1_N.poke(4)
//        println("*******************")
        println(s"****** i = ${i}*******")
        dut.clock.step(1)

//        println(s"offset : ${dut.io.offset.peek().toString()} , bankID : ${dut.io.bankID.peek().toString()}")
//        println("*******************")
//        println("*******************")

      }

    }

  }
}



