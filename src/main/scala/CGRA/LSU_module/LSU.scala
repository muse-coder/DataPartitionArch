package CGRA.LSU_module
import chisel3.{Input, UInt, _}
import chisel3.util._
import CGRA._


class LSU  [T <: Data ] (dType : T ,addrWidth:Int =8,LSU_InstWidth:Int=102,bankNum:Int=8) extends Module {
  val lsu_crossbar_io = IO(new LsuCrossbarIO(dType = dType, addrWidth = addrWidth))
  val lsu_pe_io = IO(new LsuPeIO(dType =dType ))
  val lsu_ivg_io =  IO(new LsuIvgIO())
  val config_io = IO(new LsuConfigIO(LSU_InstWidth=LSU_InstWidth)  )


  assume(LSU_InstWidth == ( addrWidth*12 + 3 +log2Ceil(bankNum)))
  val LSU_configReg = RegInit(UInt(LSU_InstWidth.W),0.U)
//  LSU_configReg := io.LSU_config
  val configVec = LSU_configReg.asTypeOf(MixedVec(Seq(
    UInt(addrWidth.W), //stride1
    UInt(addrWidth.W), //stride0
    UInt(addrWidth.W),//start1
    UInt(addrWidth.W), //start0
    UInt(addrWidth.W), //max1
    UInt(addrWidth.W), //max0
    UInt(log2Ceil(bankNum).W), // N
    UInt(addrWidth.W),//log2_B// N
    UInt(addrWidth.W), //d1_N_B_B
    UInt(addrWidth.W),//alpha0
    UInt(addrWidth.W),//alpha1
    UInt(addrWidth.W),//B
    UInt(addrWidth.W), //log2_N_B// N

    UInt(1.W),//storeSel 1  bit1 from load  bit0 from Pe
    UInt(1.W),// mode  store 1 or load 0
    Bool(),//readFifo 1
  )))
val stride1 = configVec(0)
  val stride0 = configVec(1)
  val start1 = configVec(2)
  val start0 = configVec(3)
  val max1 = configVec(4)
  val max0 = configVec(5)
  val N = configVec(6)
  val log2_B = configVec(7)
  val d1_N_B_B = configVec(8)
  val alpha0 = configVec(9)
  val alpha1 = configVec(10)
  val B = configVec(11)
  val log2_N_B = configVec(12)
  val storeSel = configVec(13)
  val  mode = configVec(14)
  val readFifo = configVec(15)


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
  when (storeSel===1.U){
    STR := LDR
  }.otherwise{
    STR := lsu_pe_io.dataFromPE
  }

  STRValid := mode

  lsu_crossbar_io.dataOutValid := STRValid
//  val Fifo = Module(new Custom_Fifo(gen = dType,depth = 2))
//  Fifo.io.enq.bits := LDR
//  Fifo.io.enq.valid:= LDR_Valid
//  Fifo.io.deq.ready:= readFifo
//  lsu_pe_io.dataToPE := Fifo.io.deq.bits
  lsu_pe_io.dataToPE := LDR
//  lsu_pe_io.valid := Fifo.io.deq.valid
  lsu_crossbar_io.dataToCrossbar := STR


  val AG_u = Module(new AG(addrWidth = addrWidth, bankNum = bankNum ))
  AG_u.io.en   := lsu_ivg_io.maxj
  AG_u.io.stride1 := stride1
  AG_u.io.stride0 := stride0
  AG_u.io.start1 := start1
  AG_u.io.start0 := start0
  AG_u.io.max1 := max1
  AG_u.io.max0 := max0
  AG_u.io.N := N
  AG_u.io.log2_B   := log2_B
  AG_u.io.d1_N_B_B  := d1_N_B_B
  AG_u.io.alpha0   := alpha0
  AG_u.io.alpha1  := alpha1
  AG_u.io.B := B
  AG_u.io.log2_N_B := log2_N_B
  lsu_crossbar_io.bankID := AG_u.io.bankID
  lsu_crossbar_io.addresToCrossbar := AG_u.io.offset

//  PrintReg()
//  PrintFifo()
 def PrintConfig(): Unit = {
   printf(p"configBus : ${Binary(config_io.bus)}\n")
   printf(p"configEn : ${Binary(config_io.en)}\n")

//   printf(p"bi : ${Binary(bi)}\n")
//   printf(p"bj : ${Binary(bj)}\n")
//   printf(p"STB : ${Binary(STB)}\n")
//   printf(p"N : ${Binary(N)}\n")
//   printf(p"log2_N : ${Binary(log2_N)}\n")
//   printf(p"d1_N : ${Binary(d1_N)}\n")
//   printf(p"storeSel : ${Binary(storeSel)}\n")
//   printf(p" mode : ${Binary( mode)}\n")
 }

  def PrintReg(): Unit = {
    printf(p"LDR : ${(LDR.asUInt)}\n")
    printf(p"STR : ${(STR.asUInt)}\n")
    printf(p"bank : ${lsu_crossbar_io.dataFromCrossbar}\n")
    printf(p"bank valid : ${lsu_crossbar_io.dataInValid}\n")
    printf(p"Pe data: ${lsu_pe_io.dataFromPE}\n")
     }

//  def PrintFifo(): Unit = {
//    printf(p"Fifo Din: ${Fifo.io.enq.bits}\n")
//    printf(p"Din Valid : ${Fifo.io.enq.valid}\n")
//    printf(p"Din Ready : ${Fifo.io.enq.ready}\n")
//
//    printf(p"Fifo Dout : ${Fifo.io.deq.bits}\n")
//    printf(p"Dout valid : ${Fifo.io.deq.valid}\n")
//    printf(p"Dout ready : ${Fifo.io.deq.ready}\n")
//  }

}

object LSU_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new LSU(dType = UInt(32.W) ,addrWidth =8,LSU_InstWidth=102,bankNum=8),
      Array(
        "--target-dir","output/LSU"
      )
    )
  )
}
