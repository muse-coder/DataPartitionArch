package CGRA.LSU_module
import CGRA.PeStructure.Pe
import chisel3._
import chisel3.util._
class Count (depth:Int=16)extends Module{
    val io = IO(new Bundle() {
        val inc = Input(UInt(1.W))
        val rst = Input(Bool())
        val en =  Input(Bool())
        val res = Output(UInt(log2Ceil(depth).W))
    })
    val cnt = RegInit(UInt(log2Ceil(depth).W),0.U)
    when(io.en === true.B){
        when(io.rst===true.B){
            cnt := 0.U
        }.otherwise{
            cnt := cnt + io.inc
        }
    }.otherwise{
        cnt := cnt
    }
    io.res := cnt
}
class IVG (countDepth:Int=16)extends Module {
    val io = IO(new Bundle() {
        val max_i = Input(UInt(log2Ceil(countDepth).W))
        val max_j = Input(UInt(log2Ceil(countDepth).W))

        val j = Output(UInt(log2Ceil(countDepth).W))
        val i = Output(UInt(log2Ceil(countDepth).W))

    })

    val rst_j = Wire(Bool())
    val rst_i = Wire(Bool())
    val en_j = Wire(Bool())
    val en_i = Wire(Bool())

    en_j := true.B

    val Count_j = Module(new Count(depth = countDepth))

    Count_j.io.inc := 1.U
    Count_j.io.en := en_j
    Count_j.io.rst := rst_j
    when(Count_j.io.res>=io.max_j){
        rst_j := true.B
        en_i := true.B
    }.otherwise{
        rst_j := false.B
        en_i := false.B
    }

    val Count_i = Module(new Count(depth = countDepth))


    when(Count_i.io.res >= io.max_i) {
        rst_i := true.B
    }.otherwise {
        rst_i := false.B
    }
    Count_i.io.inc := 1.U
    Count_i.io.en := en_i
    Count_i.io.rst := rst_i

    io.j := Count_j.io.res
    io.i := Count_i.io.res

    printf(p"Print during simulation: Count_j.io.inc is ${Count_j.io.inc}\n")
    printf(p"Print during simulation: CCount_j.io.en is ${Count_j.io.en} \n")
    printf(p"Print during simulation: Count_j.io.rst is ${Count_j.io.rst}\n")
    printf(p"Print during simulation: Count_j.io.res is ${Count_j.io.res}\n")
}



object IVG_u extends App {
    // These lines generate the Verilog output
    println(
        new (chisel3.stage.ChiselStage).emitVerilog(
            new IVG (countDepth=16 ),
            Array(
                "--target-dir", "output/"+"IVG"
            )
        )
    )
}
//
//package CGRA.LSU_module
//import CGRA.PeStructure.Pe
//import chisel3._
//import chisel3.util._
//class Count (depth:Int=32,incWidth:Int=2)extends Module{
//    val io = IO(new Bundle() {
//        val inc = Input(UInt(incWidth.W))
//        val rst = Input(Bool())
//        val en =  Input(Bool())
//        val res = Output(UInt(log2Ceil(depth).W))
//    })
//    val cnt = RegInit(UInt(log2Ceil(depth).W),0.U)
//    when(io.en === true.B){
//        when(io.rst===true.B){
//            cnt := 0.U
//        }.otherwise{
//            cnt := cnt + io.inc
//        }
//    }.otherwise{
//        cnt := cnt
//    }
//    io.res := cnt
//}
//class IVG (addrWidth:Int =10 ,countDepth:Int=32)extends Module {
//    val io = IO(new Bundle() {
//        val AS_i = Input(UInt(addrWidth.W))
//        val AS_j = Input(UInt(addrWidth.W))
//        val SA_i = Input(UInt(addrWidth.W))
//        val SA_j = Input(UInt(addrWidth.W))
//        val max_i = Input(UInt(log2Ceil(countDepth).W))
//        val max_j = Input(UInt(log2Ceil(countDepth).W))
//
//        val x0 = Output(UInt(addrWidth.W))
//        val x1 = Output(UInt(addrWidth.W))
//        val x = Output(UInt(addrWidth.W))
//
//    })
//
//    val rst_j = Wire(Bool())
//    val rst_i = Wire(Bool())
//    val en_j = Wire(Bool())
//    val en_i = Wire(Bool())
//
//    en_j := true.B
//
//    val Count_j = Module(new Count(depth = countDepth, incWidth = addrWidth))
//
//    Count_j.io.inc := io.AS_j
//    Count_j.io.en := en_j
//    Count_j.io.rst := rst_j
//    when(Count_j.io.res>=io.max_j){
//        rst_j := true.B
//        en_i := true.B
//    }.otherwise{
//        rst_j := false.B
//        en_i := false.B
//    }
//
//    val Count_i = Module(new Count(depth = countDepth, incWidth = addrWidth))
//
//
//    when(Count_i.io.res >= io.max_i) {
//        rst_i := true.B
//    }.otherwise {
//        rst_i := false.B
//    }
//    Count_i.io.inc := io.AS_i
//    Count_i.io.en := en_i
//    Count_i.io.rst := rst_i
//
//    io.x1 := Count_j.io.res + io.SA_j
//    io.x0 := Count_i.io.res + io.SA_i
//    io.x := io.x0 + io.x1
//
//    printf(p"Print during simulation: Count_j.io.inc is ${Count_j.io.inc}\n")
//    printf(p"Print during simulation: CCount_j.io.en is ${Count_j.io.en} \n")
//    printf(p"Print during simulation: Count_j.io.rst is ${Count_j.io.rst}\n")
//    printf(p"Print during simulation: Count_j.io.res is ${Count_j.io.res}\n")
//}
//
//
//
//object IVG_u extends App {
//    // These lines generate the Verilog output
//    println(
//        new (chisel3.stage.ChiselStage).emitVerilog(
//            new IVG (addrWidth=10 ),
//            Array(
//                "--target-dir", "output/"+"IVG"
//            )
//        )
//    )
//}