package CGRA.LSU_module
import chisel3._
import chisel3.util._
//package fifo

/**
 * FIFO IO with enqueue and dequeue ports using the ready/valid interface.
 */
class FifoIO[T <: Data](private val gen: T) extends Bundle {
  val enq = Flipped(new DecoupledIO(gen))
  val deq = new DecoupledIO(gen)
}

/**
 * Base class for all FIFOs.
 */
abstract class Fifo[T <: Data](gen: T, depth: Int) extends Module {
  val io = IO(new FifoIO(gen))
  assert(depth > 0, "Number of buffer elements needs to be larger than 0")
}

/**
 * FIFO with read and write pointer using dedicated registers as memory.
 */
class Custom_Fifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {

  def counter(depth: Int, incr: Bool): (UInt, UInt) = {
    val cntReg = RegInit(0.U(log2Ceil(depth).W))
    val nextVal = Mux(cntReg === (depth-1).U, 0.U, cntReg + 1.U)
    when (incr) {
      cntReg := nextVal
    }
    (cntReg, nextVal)
  }

  // the register based memory
  val memReg = SyncReadMem(depth, gen)

  val incrRead = WireInit(false.B)
  val incrWrite = WireInit(false.B)
  val (readPtr, nextRead) = counter(depth, incrRead)
  val (writePtr, nextWrite) = counter(depth, incrWrite)

  val emptyReg = RegInit(true.B)
  val fullReg = RegInit(false.B)

  when (io.enq.valid && !fullReg) {
    memReg(writePtr) := io.enq.bits
    emptyReg := false.B
    fullReg := nextWrite === readPtr
    incrWrite := true.B
  }

  when (io.deq.ready && !emptyReg) {
    fullReg := false.B
    emptyReg := nextRead === writePtr
    incrRead := true.B
  }

  io.deq.bits := memReg(readPtr)
  io.enq.ready := !fullReg
  io.deq.valid := !emptyReg
//  io.enq.
}

class ScaledFifo[T <: Data](gen: T, depth: Int) extends Fifo(gen: T, depth: Int) {
  val scaledDepth = IO(Input(UInt(log2Ceil(depth).W)))
  def counter(depth: Int, incr: Bool): (UInt, UInt) = {
    val cntReg = RegInit(0.U(log2Ceil(depth).W))
    val nextVal = Mux(cntReg === (depth-1).U, 0.U, cntReg + 1.U)
    when (incr) {
      cntReg := nextVal
    }
    (cntReg, nextVal)
  }

  // the register based memory
  val memReg = SyncReadMem(depth, gen)
  val countReg = RegInit(UInt(log2Ceil(depth).W),0.U)
  val incrRead = WireInit(false.B)
  val incrWrite = WireInit(false.B)

  when (incrRead === true.B && incrWrite === true.B){
    countReg := countReg
  }.elsewhen(incrRead === true.B && incrWrite === false.B){
    countReg := countReg - 1.U
  }.elsewhen(incrRead === false.B && incrWrite === true.B) {
    countReg := countReg + 1.U
  }.otherwise{
    countReg := countReg
  }

  val (readPtr, nextRead) = counter(depth, incrRead)
  val (writePtr, nextWrite) = counter(depth, incrWrite)

//  val emptyReg = RegInit(true.B)
//  val fullReg = RegInit(false.B)

  val emptySignal = Wire(Bool())
  val fullSignal = Wire(Bool())
  when(countReg===0.U){
    emptySignal := true.B
    fullSignal := false.B
  }.elsewhen(countReg===scaledDepth){
    fullSignal := true.B
    emptySignal := false.B
  }.otherwise{
    fullSignal := false.B
    emptySignal := false.B
  }

  when (io.enq.valid && !fullSignal) {
    memReg(writePtr) := io.enq.bits
//    emptySignal := false.B
//    fullReg := nextWrite === readPtr
//    when(countReg === scaledDepth){
//      fullSignal := true.B
//    }.otherwise{
//      fullSignal := false.B
//    }
//    fullReg := (countReg === scaledDepth)
    incrWrite := true.B
  }

  when (io.deq.ready && !emptySignal) {
//    fullSignal := false.B
//    emptySignal := (countReg === 0.U)
    incrRead := true.B
  }

  io.deq.bits := memReg(readPtr)
  io.enq.ready := !fullSignal
  io.deq.valid := !emptySignal
  //  io.enq.
//  PrintFifo()
  def PrintFifo(): Unit = {
    printf(p"Fifo Din: ${io.enq.bits}\n")
    printf(p"Din Valid : ${io.enq.valid}\n")
    printf(p"Din Ready : ${io.enq.ready}\n")

    printf(p"Fifo Dout : ${io.deq.bits}\n")
    printf(p"Dout valid : ${io.deq.valid}\n")
    printf(p"Dout ready : ${io.deq.ready}\n")
//    printf(p"fullReg : ${fullReg.asBool}\n")
//    printf(p"emptyReg : ${emptyReg.asBool}\n")
    printf(p"fullSignal : ${fullSignal.asBool}\n")
    printf(p"emptySignal : ${emptySignal.asBool}\n")
    printf(p"scaledDepth : ${scaledDepth.asUInt}\n")
    printf(p"countReg : ${countReg.asUInt}\n")

  }

}


object Custom_Fifo extends App{
  println(
    new (chisel3.stage.ChiselStage).emitVerilog(
      new Custom_Fifo(depth = 16,gen = UInt(32.W)),
      Array(
        "--target-dir","output/Custom_Fifo"
      )
    )
  )
}