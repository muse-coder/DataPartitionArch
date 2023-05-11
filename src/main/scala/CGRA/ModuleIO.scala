package CGRA
import chisel3._
import chisel3.util._

class SaguIO(addrWidth:Int,countDepth:Int) extends Bundle{
  val S1 = Input(UInt(log2Ceil(countDepth).W))
  val S2 = Input(UInt(log2Ceil(countDepth).W))
  val SA = Input(UInt(addrWidth.W))
  val maxj = Input(Bool())
  val  StreamAddress= Output(UInt(addrWidth.W))

}
class BaguIO(addrWidth:Int,bankNum:Int) extends Bundle{
  val  StreamAddress= Input(UInt(addrWidth.W))
  val N = Input(UInt(log2Ceil(bankNum).W))
  val log2_B = Input(UInt(log2Ceil(bankNum).W))
  val B = Input(UInt(log2Ceil(bankNum).W))
  val log2_N_B = Input(UInt(log2Ceil(bankNum*bankNum).W))
  val bankID = Output(UInt(log2Ceil(bankNum).W))
  val offset = Output(UInt(addrWidth.W))
}

class  ExternalIO (dataWidth:Int,configWidth:Int) extends Bundle {
  val external_data_in = Input(UInt(dataWidth.W))
  val external_control = Input(UInt(configWidth.W))
  val external_data_out = Output(UInt(dataWidth.W))

}
class BankCrossbarIO [T <: Data ](dType:T,addrWidth:Int= 8) extends Bundle {
  val dataFromBank = Input(dType)
  val dataInValid = Input(Bool())
  val addressToBank = Output(UInt(addrWidth.W))
  val dataToBank = Output(dType)

  val dataOutValid = Output(Bool())
  val mode = Output(Bool())  //mode =true : write  mode = false :read
}

class LsuCrossbarIO[T<:Data] (dType :T,addrWidth :Int) extends Bundle {
  val dataFromCrossbar = Input(dType)
  val dataInValid = Input(Bool())

  val dataToCrossbar = Output(dType)
  val dataOutValid = Output(Bool())

  val addresToCrossbar = Output(UInt(addrWidth.W))
  val bankID = Output(UInt(3.W))
  val readOrWrite = Output(UInt(1.W))
}

class LsuPeIO[T<:Data] (dType :T) extends Bundle {
  val dataFromPE = Input(dType)
//  val readFifo = Input(Bool())
  val dataToPE = Output(dType)
//  val valid = Output(Bool())
}

class LsuIvgIO (countDepth:Int=16) extends Bundle {
//  val i = Input(UInt(log2Ceil(countDepth).W))
//  val j = Input(UInt(log2Ceil(countDepth).W))
  val maxj = Input(Bool())
}

class IvgConfigIO(countDepth:Int) extends Bundle{
  val max_i = Input(UInt(log2Ceil(countDepth).W))
  val max_j = Input(UInt(log2Ceil(countDepth).W))
  val en = Input(UInt(1.W))
}

class PeArrayIO[T<:Data](row:Int,column:Int,instWidth:Int,dType:T) extends Bundle{
  val PErowConfig = Input(Vec(row, UInt(instWidth.W)))
  val config_sel = Input(Vec(row, UInt(2.W)))
  val row_left_in = Input(Vec(row, dType))
  val row_up_in = Input(Vec(column, dType))
  val row_left_out = Output(Vec(row, dType))
  val row_up_out = Output(Vec(column, dType))
}

class LsuConfigIO(LSU_InstWidth:Int) extends Bundle{
  val bus = Input(UInt(LSU_InstWidth.W)) //bi 8
  val en = Input(UInt(1.W))
}