package CGRA
import CGRA._
import chisel3._
import chisel3.util._

import scala.math._
class DataMem [T<:Data] (dataWidth:Int,addrWidth:Int)extends Module {
  val external_io = IO(new ExternalIO(dataWidth = dataWidth, configWidth = 11))

  val bankCrossbar_io = IO(Vec(8,Flipped(new BankCrossbarIO(dType = UInt(32.W),addrWidth = addrWidth))))

  val bank_select = Wire(Vec(8,UInt(1.W)))

  val external_output_sel = external_io.external_control(8+2,8)
  val data_to_bank = Wire(Vec(8,UInt(dataWidth.W)))

  val depth = pow(2, addrWidth).toInt
//  val Srams = List.fill(8)(SyncReadMem(depth, UInt(32.W)))
  val Srams = List.fill(8)(Module( new (Ram_256_words)))
  val readValidReg = Reg(Vec(8,Bool()))
//  Srams(1).write()
  for (i <- 0 until 8){
    bank_select(i) := external_io.external_control(i)
    when(bank_select(i)===1.U){
      data_to_bank(i) := bankCrossbar_io(i).dataToBank
    }.otherwise{
      data_to_bank(i) := external_io.external_data_in
    }
//    when(bankCrossbar_io(i).mode===true.B && bankCrossbar_io(i).dataOutValid===true.B){
//      Srams(i).write(bankCrossbar_io(i).addressToBank,data_to_bank(i))
//    }
    Srams(i).io.clk0 := clock
    Srams(i).io.clk1 := clock
    Srams(i).io.csb0 := (bankCrossbar_io(i).mode & bankCrossbar_io(i).dataOutValid)
    Srams(i).io.addr0 := bankCrossbar_io(i).addressToBank
    Srams(i).io.addr1 := bankCrossbar_io(i).addressToBank
    Srams(i).io.csb1 := ~(bankCrossbar_io(i).mode)
    Srams(i).io.din0 := data_to_bank(i)
//    bankCrossbar_io(i).dataFromBank := Srams(i).read(bankCrossbar_io(i).addressToBank)
    bankCrossbar_io(i).dataFromBank := Srams(i).io.dout1

    readValidReg(i) := ~(bankCrossbar_io(i).mode)
    bankCrossbar_io(i).dataInValid := readValidReg(i)
  }

  external_io.external_data_out := MuxLookup(external_output_sel, 0.U, Array(
    0.U -> bankCrossbar_io(0).dataFromBank,
    1.U -> bankCrossbar_io(1).dataFromBank,
    2.U -> bankCrossbar_io(2).dataFromBank,
    3.U -> bankCrossbar_io(3).dataFromBank,
    4.U -> bankCrossbar_io(4).dataFromBank,
    5.U -> bankCrossbar_io(5).dataFromBank,
    6.U -> bankCrossbar_io(6).dataFromBank,
    7.U -> bankCrossbar_io(7).dataFromBank,
  ))

}



object DataMem_u extends App {
  // These lines generate the Verilog output
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new DataMem(dataWidth = 32,addrWidth = 8) ,
      Array(
        "--target-dir", "output/"+"DataMem"
      )
    )
  )
}