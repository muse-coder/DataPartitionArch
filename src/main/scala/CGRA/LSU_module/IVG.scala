package CGRA.LSU_module
import CGRA.PeStructure.Pe
import chisel3._
import chisel3.util._
import CGRA._
class Count (addrWidth:Int=8)extends Module{
    val io = IO(new Bundle() {
        val inc = Input(UInt(1.W))
        val rst = Input(Bool())
        val en =  Input(Bool())
        val res = Output(UInt(addrWidth.W))
    })
    val cnt = RegInit(UInt(addrWidth.W),0.U)
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

class IVG (addrWidth:Int=8)extends Module {
    val config_io = IO(new IvgConfigIO(addrWidth= addrWidth))

    val ivg_lsu_io = IO(Flipped(new LsuIvgIO()))

    val configReg = RegInit(UInt((2*(addrWidth)).W),0.U)
    when (config_io.en===1.U){
        configReg := Cat(config_io.max_j,config_io.max_i)
    }.otherwise{
        configReg := configReg
    }
    val configVec = configReg.asTypeOf(MixedVec(Seq(
        UInt(addrWidth.W),
        UInt(addrWidth.W)
    )))
    val max_i = configVec(0)
    val max_j = configVec(1)

    val rst_j = Wire(Bool())
    val rst_i = Wire(Bool())
    val en_j = Wire(Bool())
    val en_i = Wire(Bool())

    en_j := true.B

    val Count_j = Module(new Count(addrWidth = addrWidth))

    Count_j.io.inc := 1.U
    Count_j.io.en := en_j
    Count_j.io.rst := rst_j
    when(Count_j.io.res>=max_j){
        rst_j := true.B
        en_i := true.B
    }.otherwise{
        rst_j := false.B
        en_i := false.B
    }

    val Count_i = Module(new Count(addrWidth = addrWidth))


    when(Count_i.io.res >= max_i) {
        rst_i := true.B
    }.otherwise {
        rst_i := false.B
    }
    Count_i.io.inc := 1.U
    Count_i.io.en := en_i
    Count_i.io.rst := rst_i

//    ivg_lsu_io.j := Count_j.io.res
//    ivg_lsu_io.i := Count_i.io.res

    ivg_lsu_io.maxj := en_i

    printf(p"Print during simulation: Count_j.io.inc is ${Count_j.io.inc}\n")
    printf(p"Print during simulation: CCount_j.io.en is ${Count_j.io.en} \n")
    printf(p"Print during simulation: Count_j.io.rst is ${Count_j.io.rst}\n")
    printf(p"Print during simulation: Count_j.io.res is ${Count_j.io.res}\n")
}



object IVG_u extends App {
    // These lines generate the Verilog output
    println(
        new (chisel3.stage.ChiselStage).emitVerilog(
            new IVG (addrWidth=8 ),
            Array(
                "--target-dir", "output/"+"IVG"
            )
        )
    )
}
