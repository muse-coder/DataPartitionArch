package CGRA.LSU_module
import chisel3.{UInt, _}
import chisel3.util._

class LSU  [T <: Data ] (dType : T ,addrWidth:Int =8,LSU_InstWidth:Int=40,bankNum:Int=8,countDepth:Int=16) extends Module {
  val io = IO(new Bundle() {
        val LSU_config = Input(UInt(LSU_InstWidth.W))
        val IVG_j = Input(UInt(addrWidth.W))
        val IVG_i = Input(UInt(addrWidth.W))
        val dataFromPE = Input(dType)
        val dataToPE = Output(dType)
        val dataFromBank = Input(dType)
        val dataToBank = Output(dType)
        val addresToBank = Output(UInt(addrWidth.W))
        val bankID = Output(UInt(addrWidth.W))
        val bankStoreSelect = Output(UInt(3.W))
        val bankLoadSelect = Output(UInt(3.W))
  })
//  val AG_configWidth =addrWidth*3+log2Ceil(addrWidth)
  assume(LSU_InstWidth == ( addrWidth*3 + 3*log2Ceil(bankNum) + 7))
  val LSU_configReg = RegInit(UInt(( addrWidth*3 + 3*log2Ceil(bankNum) + 7).W),0.U)
  LSU_configReg := io.LSU_config

  val confifVec = LSU_configReg.asTypeOf(MixedVec(Seq(
    UInt(addrWidth.W), //bi 8
    UInt(addrWidth.W), //bj 8
    UInt(log2Ceil(bankNum).W),//STB 3
    UInt(log2Ceil(bankNum).W),//N 3
    UInt(log2Ceil(bankNum).W),//log2_N 3
    UInt(addrWidth.W),//d1_N 8
    UInt(1.W),//store_sel 1
    UInt(1.W),//store or load
    UInt(3.W) //bank_load_select 3
  )))
  val bi = confifVec(0)
  val bj = confifVec(1)
  val STB = confifVec(2)
  val N = confifVec(3)
  val log2_N = confifVec(4)
  val d1_N = confifVec(5)
  val store_sel = confifVec(6)
  val bank_Store_Select = confifVec(7)
  val bank_Load_Select = confifVec(8)
  val LDR = RegNext(io.dataFromBank)
  val STR = RegInit(dType,0.U)
  io.bankLoadSelect := bank_Load_Select
  io.bankStoreSelect := bank_Store_Select
  when (store_sel===1.U){
    STR := LDR
  }.otherwise{
    STR := io.dataFromPE
  }
  val Fifo = Module(new Custom_Fifo(gen = dType,depth = 16))
  Fifo.io.enq.bits := LDR
  Fifo.io.enq.valid:= 1.U
  Fifo.io.deq.ready:= 1.U
  io.dataToPE := Fifo.io.deq.bits

  io.dataToBank := STR


  val AG_u = Module(new AG(addrWidth = addrWidth,countDepth = countDepth, bankNum = bankNum ))
  AG_u.io.i := io.IVG_i
  AG_u.io.j :=io.IVG_j
  AG_u.io.bj := bj
  AG_u.io.bi := bi
  AG_u.io.STB := STB
  AG_u.io.N:=N
  AG_u.io.log2_N := log2_N
  AG_u.io.d1_N:=d1_N
  io.bankID := AG_u.io.bankID
  io.addresToBank := AG_u.io.offset

}

object LSU_u extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new LSU(dType = UInt(32.W) ,addrWidth =8,LSU_InstWidth=40,bankNum=8,countDepth=16),
      Array(
        "--target-dir","output/LSU"
      )
    )
  )
}
