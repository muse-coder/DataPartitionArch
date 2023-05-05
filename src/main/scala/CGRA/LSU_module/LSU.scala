package CGRA.LSU_module
import chisel3.{Input, UInt, _}
import chisel3.util._

class LsuCrossbarIO[T<:Data] (dType :T,addrWidth :Int) extends Bundle {
  val dataFromBank = Input(dType)
  val dataToBank = Output(dType)
  val addresToBank = Output(UInt(addrWidth.W))
  val bankID = Output(UInt(3.W))
  val readOrWrite = Output(UInt(1.W))
  val dataValid = Input(Bool())
}

class LsuPeIO[T<:Data] (dType :T) extends Bundle {
  val dataFromPE = Input(dType)
  val readFifo = Input(Bool())
  val dataToPE = Output(dType)
  val valid = Output(Bool())
}

class LsuIvgIO (countDepth:Int=16) extends Bundle {
  val i = Input(UInt(log2Ceil(countDepth).W))
  val j = Input(UInt(log2Ceil(countDepth).W))
}


class LSU  [T <: Data ] (dType : T ,addrWidth:Int =8,LSU_InstWidth:Int=35,bankNum:Int=8,countDepth:Int=16) extends Module {
  val lsu_crossbar_io = IO(new LsuCrossbarIO(dType = dType, addrWidth = addrWidth))
  val lsu_pe_io = IO(new LsuPeIO(dType =dType ))
  val lsu_ivg_io =  IO(new LsuIvgIO(countDepth = countDepth))
  val config_io =  IO(new Bundle {
    val bus = Input(UInt(LSU_InstWidth.W)) //bi 8
    val en = Input(UInt(1.W))
  })


  assume(LSU_InstWidth == ( addrWidth*3 + 3*log2Ceil(bankNum) + 2))
  val LSU_configReg = RegInit(UInt(LSU_InstWidth.W),0.U)
//  LSU_configReg := io.LSU_config
  val configVec = LSU_configReg.asTypeOf(MixedVec(Seq(
    UInt(addrWidth.W), //bi 8
    UInt(addrWidth.W), //bj 8
    UInt(log2Ceil(bankNum).W),//STB 3
    UInt(log2Ceil(bankNum).W),//N 3
    UInt(log2Ceil(bankNum).W),//log2_N 3
    UInt(addrWidth.W),//d1_N 8
    UInt(1.W),//storeSel 1  bit1 from load  bit0 from Pe
    UInt(1.W),//access  store 1 or load 0
  )))
  val bi = configVec(0)
  val bj = configVec(1)
  val STB = configVec(2)
  val N = configVec(3)
  val log2_N = configVec(4)
  val d1_N = configVec(5)
  val storeSel = configVec(6)
  val access = configVec(7)



  val en = config_io.en
  lsu_crossbar_io.readOrWrite := access
  when (en ===1.U){
    LSU_configReg := config_io.bus
  }.otherwise{
    LSU_configReg := LSU_configReg
  }

  val LDR = RegInit(dType,0.U)
  val LDR_Valid = RegNext(lsu_crossbar_io.dataValid)
  when(lsu_crossbar_io.dataValid===true.B){
    LDR := lsu_crossbar_io.dataFromBank
  }.otherwise{
    LDR := LDR
  }

  val STR = RegInit(dType,0.U)

  when (storeSel===1.U){
    STR := LDR
  }.otherwise{
    STR := lsu_pe_io.dataFromPE
  }
  val Fifo = Module(new Custom_Fifo(gen = dType,depth = 16))
  Fifo.io.enq.bits := LDR
  Fifo.io.enq.valid:= LDR_Valid
  Fifo.io.deq.ready:= lsu_pe_io.readFifo
  lsu_pe_io.dataToPE := Fifo.io.deq.bits
  lsu_pe_io.valid := Fifo.io.deq.valid
  lsu_crossbar_io.dataToBank := STR


  val AG_u = Module(new AG(addrWidth = addrWidth,countDepth = countDepth, bankNum = bankNum ))
  AG_u.io.i := lsu_ivg_io.i
  AG_u.io.j := lsu_ivg_io.j
  AG_u.io.bj := bj
  AG_u.io.bi := bi
  AG_u.io.STB := STB
  AG_u.io.N:=N
  AG_u.io.log2_N := log2_N
  AG_u.io.d1_N:=d1_N
  lsu_crossbar_io.bankID := AG_u.io.bankID
  lsu_crossbar_io.addresToBank := AG_u.io.offset
//  PrintReg()
  PrintFifo()
 def PrintConfig(): Unit = {
   printf(p"configBus : ${Binary(config_io.bus)}\n")
   printf(p"configEn : ${Binary(config_io.en)}\n")

   printf(p"bi : ${Binary(bi)}\n")
   printf(p"bj : ${Binary(bj)}\n")
   printf(p"STB : ${Binary(STB)}\n")
   printf(p"N : ${Binary(N)}\n")
   printf(p"log2_N : ${Binary(log2_N)}\n")
   printf(p"d1_N : ${Binary(d1_N)}\n")
   printf(p"storeSel : ${Binary(storeSel)}\n")
   printf(p"access : ${Binary(access)}\n")
 }

  def PrintReg(): Unit = {
    printf(p"LDR : ${(LDR.asUInt)}\n")
    printf(p"STR : ${(STR.asUInt)}\n")
    printf(p"bank : ${lsu_crossbar_io.dataFromBank}\n")
    printf(p"bank valid : ${lsu_crossbar_io.dataValid}\n")
    printf(p"Pe data: ${lsu_pe_io.dataFromPE}\n")
     }

  def PrintFifo(): Unit = {
    printf(p"Fifo Din: ${Fifo.io.enq.bits}\n")
    printf(p"Din Valid : ${Fifo.io.enq.valid}\n")
    printf(p"Din Ready : ${Fifo.io.enq.ready}\n")

    printf(p"Fifo Dout : ${Fifo.io.deq.bits}\n")
    printf(p"Dout valid : ${Fifo.io.deq.valid}\n")
    printf(p"Dout ready : ${Fifo.io.deq.ready}\n")
  }

}

object LSU_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new LSU(dType = UInt(32.W) ,addrWidth =8,LSU_InstWidth=35,bankNum=8,countDepth=16),
      Array(
        "--target-dir","output/LSU"
      )
    )
  )
}
