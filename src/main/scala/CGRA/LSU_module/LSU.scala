package CGRA.LSU_module
import chisel3.{Input, UInt, _}
import chisel3.util._
import CGRA._


class LSU  [T <: Data ] (dType : T ,addrWidth:Int =8,LSU_InstWidth:Int=68,bankNum:Int=8,fifoDepth:Int = 8) extends Module {
  val lsu_crossbar_io = IO(new LsuCrossbarIO(dType = dType, addrWidth = addrWidth))
  val lsu_pe_io = IO(new LsuPeIO(dType =dType ))
  val lsu_ivg_io =  IO(new LsuIvgIO())
  val config_io = IO(new LsuConfigIO(LSU_InstWidth=LSU_InstWidth)   )


  assume(LSU_InstWidth == ( addrWidth * 7 + 2*log2Ceil(bankNum) + 3 + log2Ceil(fifoDepth)))
  val LSU_configReg = RegInit(UInt(LSU_InstWidth.W),0.U)

  val configVec = LSU_configReg.asTypeOf(MixedVec(Seq(
    UInt(addrWidth.W), //stride1
    UInt(addrWidth.W), //stride0
    UInt(addrWidth.W),//start1
    UInt(addrWidth.W), //start0
    UInt(addrWidth.W), //max1
    UInt(addrWidth.W), //max0
    UInt(addrWidth.W),//d1_N 8
    UInt(log2Ceil(bankNum).W), // N
    UInt(log2Ceil(bankNum).W),//log2_N 3
    UInt(1.W),//storeSel 1  bit1 from load  bit0 from Pe
    UInt(1.W),// mode  write 1 or read 0
    Bool(),//readFifo 1
    UInt(log2Ceil(fifoDepth).W) //fifoDepth 3
  )))
  val stride1 = configVec(0)
  val stride0 = configVec(1)
  val start1 = configVec(2)
  val start0 = configVec(3)
  val max1 = configVec(4)
  val max0 = configVec(5)
  val d1_N = configVec(6)
  val N = configVec(7)
  val log2_N = configVec(8)
  val storeSel = configVec(9)
  val  mode = configVec(10)
  val readFifo = configVec(11)
  val scaledDepth = configVec(12)

  val en = config_io.en
  lsu_crossbar_io.readOrWrite :=  mode
  when (en ===1.U){
    LSU_configReg := config_io.bus
  }.otherwise{
    LSU_configReg := LSU_configReg
  }

  val LDR = RegInit(dType,0.U)
  val LDR_Valid = RegNext(lsu_crossbar_io.dataInValid)
  when(lsu_crossbar_io.dataInValid===true.B){
    LDR := lsu_crossbar_io.dataFromCrossbar
  }.otherwise{
    LDR := LDR
  }

  val STR = RegInit(dType,0.U)
  val STRValid = RegInit(false.B)
  when (storeSel.asUInt === 1.U){
    STR := LDR
  }.otherwise{
    STR := lsu_pe_io.dataFromPE
  }

  STRValid := mode

  lsu_crossbar_io.dataOutValid := STRValid
  val Fifo = Module(new ScaledFifo(gen = dType,depth = 8))
  Fifo.io.enq.bits := LDR
  Fifo.io.enq.valid:= LDR_Valid
  Fifo.io.deq.ready:= readFifo
  Fifo.scaledDepth := scaledDepth
  lsu_pe_io.dataToPE := Fifo.io.deq.bits
//  lsu_pe_io.valid := Fifo.io.deq.valid
  lsu_crossbar_io.dataToCrossbar := STR


  val AG_u = Module(new AG(addrWidth = addrWidth, bankNum = bankNum ))
  AG_u.io.stride1 := stride1
  AG_u.io.stride0 := stride0
  AG_u.io.start1 := start1
  AG_u.io.start0 := start0
  AG_u.io.max1 := max1
  AG_u.io.max0 := max0
  AG_u.io.en := lsu_ivg_io.maxj
  AG_u.io.N:=N
  AG_u.io.log2_N := log2_N
  AG_u.io.d1_N:=d1_N
  lsu_crossbar_io.addresToCrossbar := AG_u.io.offset
  lsu_crossbar_io.bankID := AG_u.io.bankID
  //  PrintReg()
//  PrintFifo()
 def PrintConfig(): Unit = {
   printf(p"configBus : ${Binary(config_io.bus)}\n")
   printf(p"configEn : ${Binary(config_io.en)}\n")

   printf(p"N : ${Binary(N)}\n")
   printf(p"log2_N : ${Binary(log2_N)}\n")
   printf(p"d1_N : ${Binary(d1_N)}\n")
   printf(p"storeSel : ${Binary(storeSel)}\n")
   printf(p" mode : ${Binary( mode)}\n")
 }

  def PrintReg(): Unit = {
    printf(p"LDR : ${(LDR.asUInt)}\n")
    printf(p"STR : ${(STR.asUInt)}\n")
    printf(p"bank : ${lsu_crossbar_io.dataFromCrossbar}\n")
    printf(p"bank valid : ${lsu_crossbar_io.dataInValid}\n")
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
      new LSU(dType = UInt(32.W) ,addrWidth =8,LSU_InstWidth=68,bankNum=8),
      Array(
        "--target-dir","output/LSU"
      )
    )
  )
}
