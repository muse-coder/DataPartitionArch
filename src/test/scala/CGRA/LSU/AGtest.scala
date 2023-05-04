package fifo

import CGRA.LSU_module._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec



class AGtest extends AnyFlatSpec with ChiselScalatestTester {

  "AG test" should "pass" in {

    test(new AG (addresWidth =10 )) { dut =>
      val randomNum = scala.util.Random
      for (i<-0 until 400){
        val a = randomNum.nextInt(10)+1
        val b = randomNum.nextInt(10)+1
        val c = randomNum.nextInt(10)+1
        val d = randomNum.nextInt(10)+1

        dut.io.x0.poke(a.U)
        dut.io.x1.poke(b.U)
        dut.io.x.poke((a+b).U)
        dut.io.d1_N.poke(c.U)
        dut.io.log2_N.poke(4.U)
        dut.io.N.poke(33.U)
        dut.io.STB.poke(d.U)
        dut.clock.step(1)
        println("*******************")
        println("*******************")
        println(s"x0 : ${dut.io.x0.peek().toString()} , x1 : ${dut.io.x1.peek().toString()}")
        println(s"x : ${dut.io.x.peek().toString()} , d1_N : ${dut.io.d1_N.peek().toString()}")
        println(s"log2_N : ${dut.io.log2_N.peek().toString()} , N : ${dut.io.N.peek().toString()}")
        println(s"STB : ${dut.io.STB.peek().toString()} ")
        println(s"offset : ${dut.io.offset.peek().toString()} , bankID : ${dut.io.bankID.peek().toString()}")
        println("*******************")
        println("*******************")

      }

    }

  }
}



