
// See LICENSE for license details.
package openmpe

// import Chisel._
import chisel3._
import chisel3.util._
import chisel3.experimental.{Analog,attach}
import freechips.rocketchip.amba._
import freechips.rocketchip.amba.axi4._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.subsystem._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.interrupts._
// import sifive.fpgashells.ip.xilinx.vc707mig.{VC707MIGIOClocksReset, VC707MIGIODDR, vc707mig}
import freechips.rocketchip.util._
import freechips.rocketchip.regmapper.{HasRegMap, RegField}

// import sifive.fpgashells.ip.xilinx.Series7MMCM
// import sifive.fpgashells.clocks._
// import sifive.blocks.devices.nvmmctr._
// import openmpe._

// Ports.scala

//   val mem_axi4 = InModuleBody { memAXI4Node.makeIOs() }
//   val axi4Bundles = memAXI4Node.out.map(_._1)
//   val bbModule = new AXIMPE()  
//   // val mem_axi4 = InModuleBody { bbModule.makeIOs() }


// class SimAXIMPE(edge: AXI4EdgeParameters, size: BigInt, base: BigInt = 0)(implicit p: Parameters) extends SimpleLazyModule {
//   val node = AXI4MasterNode(List(edge.master))
//   val mpe = AddressSet.misaligned(base, size).map { aSet =>
//     LazyModule(new MPEWrapper(
//       address = aSet,
//       beatBytes = edge.bundle.dataBits/8,
//       wcorrupt=edge.slave.requestKeys.contains(AMBACorrupt)
//     ))
//   }
//   val xbar = AXI4Xbar()
//   // mpe.foreach{ s => s.node := AXI4Buffer() := AXI4Fragmenter() := xbar }
//   mpe.foreach{ s => s.node := AXI4Buffer() := xbar }
//   xbar := node
//   val io_axi4 = InModuleBody { node.makeIOs() }
// }


// case class XilinxVC707MIGParams(
//   address : Seq[AddressSet]
// )

// class XilinxVC707MIGPads(depth : BigInt) extends VC707MIGIODDR(depth) {
//   def this(c : XilinxVC707MIGParams) {
//     this(AddressRange.fromSets(c.address).head.size)
//   }
// }

// class XilinxVC707MIGIO(depth : BigInt) extends VC707MIGIODDR(depth) with VC707MIGIOClocksReset
// {
//   val lat_cr                = Bits(INPUT,  8)
//   val lat_cw                = Bits(INPUT,  8)
//   val lat_dr256             = Bits(INPUT,  8)
//   val lat_dr4096            = Bits(INPUT,  8)
//   val lat_dw256             = Bits(INPUT,  8)
//   val lat_dw4096            = Bits(INPUT,  8)
//   val cnt_read              = Bits(OUTPUT, 40)
//   val cnt_write             = Bits(OUTPUT, 40)
//   val cnt_bdr               = Bits(OUTPUT, 40)
//   val cnt_bdw               = Bits(OUTPUT, 40)
// }
case class MPEParams(
  address: BigInt,
  size:    BigInt  = 4096,
  raddress: BigInt = 0x60000000L
)

class MPENode(params: MPEParams)(implicit p: Parameters) extends LazyModule{
// trait MPENode extends HasRegMap{
//   val ranges = AddressRange.fromSets(List(address))
//   require (ranges.size == 1, "DDR range must be contiguous")
//   val offset = ranges.head.base
//   val depth = ranges.head.size
//   require((depth<=0x100000000L),"vc707mig supports upto 4GB depth configuraton")
//   // printf(p"Cycle: ${ranges}\n")
//   printf(p"offset: ${offset}\n")
//   printf(p"depth: ${depth}\n")
  // alias for constants



  // nvmmctr
  // implicit val p: Parameters
  // def params: MPEParams

  // val w = NVMMCTRConstant.reg_width
  // val b = NVMMCTRConstant.bank_width
  // val a = NVMMCTRConstant.addr_width
  // val l = NVMMCTRConstant.lat_width
  // val m = NVMMCTRConstant.mem_size
  // val e = NVMMCTRConstant.end_addr

  // val clock: Clock
  // val io: NVMMCTRIO

  // /* wire between RegMap <--> NVMMCTRModule */
  // //val clear      = Wire(new DecoupledIO(UInt(w.W)))
  // //val clear      = Reg(UInt(w.W))
  // val nvmm_begin = RegInit(0.U(b.W))
  // val lat_cr     = RegInit(0.U(l.W))
  // val lat_cw     = RegInit(0.U(l.W))
  // val lat_tRCD2  = RegInit(0.U(l.W))
  // val lat_tRP2   = RegInit(0.U(l.W))
  // val lat_tRAS2  = RegInit(0.U(11.W))
  // val lat_dr256  = RegInit(0.U(l.W))
  // val lat_dr4096 = RegInit(0.U(l.W))
  // val lat_dw256  = RegInit(0.U(l.W))
  // val lat_dw4096 = RegInit(0.U(l.W))
  // val cnt_read   = Reg(UInt(w.W))
  // val cnt_write  = Reg(UInt(w.W))
  // val cnt_act    = Reg(UInt(w.W))
  // //val cnt_pre    = Reg(UInt(w.W))
  // val cnt_bdr    = Reg(UInt(w.W))
  // val cnt_bdw    = Reg(UInt(w.W))





  val idBits_e  = 4
  val dataBits_e = 64
  val idBits_d   = 4
  val dataBits_d = 512

  val device = new MemoryDevice
  val data_axi_node = AXI4SlaveNode(Seq(AXI4SlavePortParameters(
      slaves = Seq(AXI4SlaveParameters(
      address       = Seq(AddressSet(params.raddress, 0x10000000L-1L)),
      resources     = device.reg,
      regionType    = RegionType.UNCACHED,
      executable    = true,
      supportsWrite = TransferSizes(1, 64),
      supportsRead  = TransferSizes(1, 64))),
      beatBytes = 8)))
      
//   val data_tl_node = data_axi_node := LazyModule(new TLToAXI4).node
//   val device = devName
//     .map(new SimpleDevice(_, None.getOrElse(Seq("sifive,sram0"))))
//     .getOrElse(new MemoryDevice())

//   val resources = device.reg("mem")

//   val node = AXI4SlaveNode(Seq(AXI4SlavePortParameters(
//     Seq(AXI4SlaveParameters(
//       address       = List(address) ++ errors,
//       resources     = resources,
//       regionType    = RegionType.UNCACHED,
//       executable    = true,
//       // supportsRead  = TransferSizes(1, beatBytes),
//       // supportsWrite = TransferSizes(1, beatBytes),
//       supportsRead  = TransferSizes(1, 64),
//       supportsWrite = TransferSizes(1, 64),
//       // interleavedId = Some(0)
//       )),
//     // beatBytes  = beatBytes,
//     beatBytes  = 8,
//     // requestKeys = if (wcorrupt) Seq(AMBACorrupt) else Seq(),
//     // minLatency = 1
//     )))

  val sram_axi_node = Some(AXI4MasterNode(Seq(AXI4MasterPortParameters(
      masters = Seq(AXI4MasterParameters(
        name    = "MPE SRAM",
        id      = IdRange(0, 256)))))))

  sram_axi_node.foreach {
    val sram = Some(LazyModule(new AXI4RAM(
      address = AddressSet(0, 0x100000L-1L),
      beatBytes = dataBits_d/8)))
    sram.get.node := _
  }


  lazy val module = new LazyModuleImp(this) {
    // val io = IO(new Bundle {
    //   val port = new XilinxVC707MIGIO(depth)
    // })

    // childClock := io.port.ui_clk
    // childReset := io.port.ui_clk_sync_rst

    //MIG black box instantiation
    // val blackbox = Module(new vc707mig(depth))
    val (axi_async, _) = data_axi_node.in(0)
    // val memAXI4Node.out(0) = (axi_async, _)//node = axi_async

    // //pins to top level

    // //inouts
    // attach(io.port.ddr3_dq,blackbox.io.ddr3_dq)
    // attach(io.port.ddr3_dqs_n,blackbox.io.ddr3_dqs_n)
    // attach(io.port.ddr3_dqs_p,blackbox.io.ddr3_dqs_p)

    // //outputs
    // io.port.ddr3_addr         := blackbox.io.ddr3_addr
    // io.port.ddr3_ba           := blackbox.io.ddr3_ba
    // io.port.ddr3_ras_n        := blackbox.io.ddr3_ras_n
    // io.port.ddr3_cas_n        := blackbox.io.ddr3_cas_n
    // io.port.ddr3_we_n         := blackbox.io.ddr3_we_n
    // io.port.ddr3_reset_n      := blackbox.io.ddr3_reset_n
    // io.port.ddr3_ck_p         := blackbox.io.ddr3_ck_p
    // io.port.ddr3_ck_n         := blackbox.io.ddr3_ck_n
    // io.port.ddr3_cke          := blackbox.io.ddr3_cke
    // io.port.ddr3_cs_n         := blackbox.io.ddr3_cs_n
    // io.port.ddr3_dm           := blackbox.io.ddr3_dm
    // io.port.ddr3_odt          := blackbox.io.ddr3_odt

    // //inputs
    // //NO_BUFFER clock
    // blackbox.io.sys_clk_i     := io.port.sys_clk_i

    // io.port.ui_clk            := blackbox.io.ui_clk
    // io.port.ui_clk_sync_rst   := blackbox.io.ui_clk_sync_rst
    // io.port.mmcm_locked       := blackbox.io.mmcm_locked
    // blackbox.io.aresetn       := io.port.aresetn

    // //misc
    // io.port.init_calib_complete := blackbox.io.init_calib_complete
    // blackbox.io.sys_rst         := io.port.sys_rst

    val awaddr = axi_async.aw.bits.addr - 0x70000000L.U + 0x40000000L.U
    val araddr = axi_async.ar.bits.addr - 0x70000000L.U + 0x40000000L.U
    // val awaddr = axi_async.aw.bits.addr
    // val araddr = axi_async.ar.bits.addr

    // latency control
    // val nvmmctr = Module(new NVMMCTRModule())

    // // Clock for MPE
    // val mpe_pll = Module(new Series7MMCM(PLLParameters(
    //   "mpePLL",
    //   PLLInClockParameters(200),     // Input: 200 MHz
    //   Seq(
    //     PLLOutClockParameters(50)   // Output 100 MHz
    //   )
    // )))
    // mpe_pll.io.clk_in1  := io.port.sys_clk_i.asClock
    // mpe_pll.io.reset    := io.port.sys_rst

    // aliases
    // val sysClk = io.port.sys_clk_i.asClock
    // val sysRst = io.port.sys_rst | childReset
    // val mpeClk = mpe_pll.io.clk_out1.get
    // val mpeRst = !io.port.aresetn
    val sysClk = clock
    val sysRst = reset.asBool
    val mpeClk = sysClk
    val mpeRst = sysRst

    // Memory Protection Engine
    // val mpe = withClockAndReset(mpeClk, mpeRst){ Module(new MPE(8)) }
    val mpe = Module(new MPE(8))



    // connections
    // axi_async <--> MPE <--> NVMMCTR(delay) <--> MIG (blackbox)

    /*
     * axi_async <--> MPE
     */
    // withClockAndReset(sysClk, sysRst){


      mpe.io.cpu.ar <> axi_async.ar
      mpe.io.cpu.ar.bits.addr := Cat(0.U(1.W), araddr(30,6))
      axi_async.r <> mpe.io.cpu.r      
      mpe.io.cpu.aw <> axi_async.aw
      mpe.io.cpu.aw.bits.addr := Cat(0.U(1.W), awaddr(30,6))
      mpe.io.cpu.w <> axi_async.w
      axi_async.b  <> mpe.io.cpu.b

      // val queueAR_e = genAsyncQueue(new AXI4BusA(idBits_e),           sysClk, sysRst, mpeClk, mpeRst)
      // val queueR_e  = genAsyncQueue(new AXI4BusR(idBits_e, dataBits_e), mpeClk, mpeRst, sysClk, sysRst)
      // val queueAW_e = genAsyncQueue(new AXI4BusA(idBits_e),           sysClk, sysRst, mpeClk, mpeRst)
      // val queueW_e  = genAsyncQueue(new AXI4BusW(dataBits_e),         sysClk, sysRst, mpeClk, mpeRst)
      // val queueB_e  = genAsyncQueue(new AXI4BusB(idBits_e),           mpeClk, mpeRst, sysClk, sysRst)

      // queueAR_e.io.enq <> axi_async.ar
      // mpe.io.cpu.ar  <> queueAR_e.io.deq
      // //31,6
      // // queueAR_e.io.enq.bits.addr := araddr(31,6)

      // queueR_e.io.enq <> mpe.io.cpu.r
      // axi_async.r   <> queueR_e.io.deq

      // queueAW_e.io.enq <> axi_async.aw
      // mpe.io.cpu.aw  <> queueAW_e.io.deq
      // //31,6
      // // queueAW_e.io.enq.bits.addr := awaddr(31,6)

      // queueW_e.io.enq <> axi_async.w
      // mpe.io.cpu.w  <> queueW_e.io.deq

      // queueB_e.io.enq <> mpe.io.cpu.b
      // axi_async.b   <> queueB_e.io.deq
    // }

    /*
     * MPE <--> NVMMCTR <--> MIG
     */
    // withClockAndReset(sysClk, sysRst){

      //call sram
      val (blackbox, _) = sram_axi_node.get.out(0)

    // AR except for valid/ready
      blackbox.ar <> mpe.io.mem.ar
      when       (mpe.io.mem.ar.bits.addr(9))  {blackbox.ar.bits.addr := Cat(6.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.ar.bits.addr(12)) {blackbox.ar.bits.addr := Cat(5.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.ar.bits.addr(15)) {blackbox.ar.bits.addr := Cat(4.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.ar.bits.addr(16)) {blackbox.ar.bits.addr := Cat(3.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.ar.bits.addr(19)) {blackbox.ar.bits.addr := Cat(2.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.ar.bits.addr(24)) {blackbox.ar.bits.addr := Cat(1.U(18.W), mpe.io.mem.ar.bits.addr(7, 0), 0.U(6.W))}
      // blackbox.ar.bits.addr     := Cat(mpe.io.mem.ar.bits.addr, 0.U(6.W))
      blackbox.ar.bits.len      := 0.U(8.W)     // 4 beat
      blackbox.ar.bits.size     := 6.U(3.W)     // 2**4 bytes/beat

      // R
      mpe.io.mem.r  <> blackbox.r

      // AW except for valid/ready
      blackbox.aw <> mpe.io.mem.aw
      when       (mpe.io.mem.aw.bits.addr(9))  {blackbox.aw.bits.addr := Cat(6.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.aw.bits.addr(12)) {blackbox.aw.bits.addr := Cat(5.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.aw.bits.addr(15)) {blackbox.aw.bits.addr := Cat(4.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.aw.bits.addr(16)) {blackbox.aw.bits.addr := Cat(3.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.aw.bits.addr(19)) {blackbox.aw.bits.addr := Cat(2.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      . elsewhen (mpe.io.mem.aw.bits.addr(24)) {blackbox.aw.bits.addr := Cat(1.U(18.W), mpe.io.mem.aw.bits.addr(7, 0), 0.U(6.W))}
      // blackbox.aw.bits.addr  := Cat(mpe.io.mem.aw.bits.addr, 0.U(6.W))
      blackbox.aw.bits.len   := 0.U(8.W)     // 1 beat
      blackbox.aw.bits.size  := 6.U(3.W)     // 2**6 bytes/beat

      // W
      blackbox.w  <> mpe.io.mem.w
      blackbox.w.bits.strb  := "xFFFFFFFFFFFFFFFF".U(64.W)
      blackbox.w.bits.last  := true.B  // only 1 beat

      // B
      mpe.io.mem.b <> blackbox.b


      // val queueAR_d = genAsyncQueue(new AXI4BusAS(idBits_d),           mpeClk, mpeRst, sysClk, sysRst)
      // val queueR_d  = genAsyncQueue(new AXI4BusRS(idBits_d, dataBits_d), sysClk, sysRst, mpeClk, mpeRst)
      // val queueAW_d = genAsyncQueue(new AXI4BusAS(idBits_d),           mpeClk, mpeRst, sysClk, sysRst)
      // val queueW_d  = genAsyncQueue(new AXI4BusWS(dataBits_d),         mpeClk, mpeRst, sysClk, sysRst)
      // val queueB_d  = genAsyncQueue(new AXI4BusBS(idBits_d),           sysClk, sysRst, mpeClk, mpeRst)


      // // AR except for valid/ready
      // queueAR_d.io.enq <> mpe.io.mem.ar
      // blackbox.ar.valid         := queueAR_d.io.deq.valid
      // queueAR_d.io.deq.ready      := blackbox.ar.ready
      // blackbox.ar.bits.id       := queueAR_d.io.deq.bits.id
      // // blackbox.ar.bits.addr     := Cat(queueAR_d.io.deq.bits.addr, 0.U(6.W))
      // blackbox.ar.bits.addr     := queueAR_d.io.deq.bits.addr
      // blackbox.ar.bits.len      := 0.U(8.W)     // 4 beat
      // blackbox.ar.bits.size     := 6.U(3.W)     // 2**4 bytes/beat
      // //blackbox.io.s_axi_arburst := "b01".U(2.W) // INCR

      // // R
      // mpe.io.mem.r  <> queueR_d.io.deq
      // queueR_d.io.enq.bits.id    := blackbox.r.bits.id
      // queueR_d.io.enq.bits.data  := blackbox.r.bits.data
      // queueR_d.io.enq.valid      := blackbox.r.valid
      // blackbox.r.ready         := queueR_d.io.enq.ready
      // // u_nvdla_cvsram.r_rlast                := cvsram.r.bits.last

      // // AW except for valid/ready
      // queueAW_d.io.enq <> mpe.io.mem.aw
      // blackbox.aw.valid         := queueAW_d.io.deq.valid
      // queueAW_d.io.deq.ready      := blackbox.aw.ready
      // blackbox.aw.bits.id    := queueAW_d.io.deq.bits.id
      // // blackbox.aw.bits.addr  := Cat(queueAW_d.io.deq.bits.addr, 0.U(6.W))
      // blackbox.aw.bits.addr  := queueAW_d.io.deq.bits.addr
      // blackbox.aw.bits.len   := 0.U(8.W)     // 1 beat
      // blackbox.aw.bits.size  := 6.U(3.W)     // 2**6 bytes/beat
      // //blackbox.io.s_axi_awburst := "b01".U(2.W) // INCR

      // // W
      // queueW_d.io.enq  <> mpe.io.mem.w
      // blackbox.w.bits.data  := queueW_d.io.deq.bits.data
      // blackbox.w.bits.strb  := "xFFFFFFFFFFFFFFFF".U(64.W)
      // blackbox.w.bits.last  := true.B  // only 1 beat
      // blackbox.w.valid := queueW_d.io.deq.valid
      // queueW_d.io.deq.ready      := blackbox.w.ready

      // // B
      // mpe.io.mem.b <> queueB_d.io.deq
      // queueB_d.io.enq.bits.id     := blackbox.b.bits.id
      // queueB_d.io.enq.valid       := blackbox.b.valid
      // blackbox.b.ready  := queueB_d.io.enq.ready

      /*
       * nvmmctr
       */
      // // AR (queue <--> nvmmctr)
      // nvmmctr.io.mbus_arvalid := queueAR_d.io.deq.valid
      // queueAR_d.io.deq.ready    := nvmmctr.io.mbus_arready
      // nvmmctr.io.mbus_araddr  := queueAR_d.io.deq.bits.addr

      // // AW (queue <--> nvmmctr)
      // nvmmctr.io.mbus_awvalid := queueAW_d.io.deq.valid
      // queueAW_d.io.deq.ready    := nvmmctr.io.mbus_awready
      // nvmmctr.io.mbus_awaddr  := queueAW_d.io.deq.bits.addr

      // // AR (nvmmctr <--> MIG)
      // blackbox.ar.valid := nvmmctr.io.mig_arvalid
      // nvmmctr.io.mig_arready    := blackbox.ar.ready

      // // AW (nvmmctr <--> MIG)
      // blackbox.aw.valid := nvmmctr.io.mig_awvalid
      // nvmmctr.io.mig_awready    := blackbox.aw.ready
    // }

    // nvmmctr.io.nvmm_begin     := 2.U(3.W)
    // nvmmctr.io.lat_cr         := lat_cr
    // nvmmctr.io.lat_cw         := lat_cw
    // //nvmmctr.io.lat_dr256      := io.port.lat_dr256
    // //nvmmctr.io.lat_dr4096     := io.port.lat_dr4096
    // //nvmmctr.io.lat_dw256      := io.port.lat_dw256
    // //nvmmctr.io.lat_dw4096     := io.port.lat_dw4096
    // nvmmctr.io.lat_dr256      := 0.U
    // nvmmctr.io.lat_dr4096     := 0.U
    // nvmmctr.io.lat_dw256      := 0.U
    // nvmmctr.io.lat_dw4096     := 0.U

  }

  // helpers
  def genAsyncQueue[T <: Bundle](gen: T, enq_clk: Clock, enq_rst: Bool,
    deq_clk: Clock, deq_rst: Bool) = {
    val q = Module(new AsyncQueue(gen, AsyncQueueParams(depth = 1)))
    q.io.enq_clock := enq_clk
    q.io.enq_reset := enq_rst
    q.io.deq_clock := deq_clk
    q.io.deq_reset := deq_rst

    q
  }
    /* register map */
  // regmap(
  //   //0x00 -> Seq(RegField.w(1, clear)),
  //   0x08 -> Seq(RegField(b, nvmm_begin)),
  //   0x10 -> Seq(RegField(l, lat_cr)),
  //   0x18 -> Seq(RegField(l, lat_cw)),
  //   0x20 -> Seq(RegField(l, lat_tRCD2)),
  //   0x28 -> Seq(RegField(l, lat_tRP2)),
  //   0x30 -> Seq(RegField(l, lat_tRAS2)),
  //   0x38 -> Seq(RegField(l, lat_dr256)),
  //   0x40 -> Seq(RegField(l, lat_dr4096)),
  //   0x48 -> Seq(RegField(l, lat_dw256)),
  //   0x50 -> Seq(RegField(l, lat_dw4096)),
  //   0x58 -> Seq(RegField.r(w, cnt_read)),
  //   0x60 -> Seq(RegField.r(w, cnt_write)),
  //   0x68 -> Seq(RegField.r(w, cnt_act)),
  //   //0x70 -> Seq(RegField.r(w, cnt_pre)),
  //   0x78 -> Seq(RegField.r(w, cnt_bdr)),
  //   0x80 -> Seq(RegField.r(w, cnt_bdw))
  // )
}
// class AXIMPENode(params: MPEParams)(implicit p: Parameters)
//   extends AXI4RegisterRouter(
//     0x1000,
//     8)(
//         new AXI4RegBundle(params, _))(
//       new AXI4RegModule(params, _, _) with MPENode)
// class XilinxVC707MIG(c : XilinxVC707MIGParams, crossing: ClockCrossingType = AsynchronousCrossing(8))(implicit p: Parameters) extends LazyModule {
//   val ranges = AddressRange.fromSets(c.address)
//   val depth = ranges.head.size

//   val buffer  = LazyModule(new TLBuffer)
//   val toaxi4  = LazyModule(new TLToAXI4(adapterName = Some("mem"), stripBits = 1))
//   val indexer = LazyModule(new AXI4IdIndexer(idBits = 4))
//   val deint   = LazyModule(new AXI4Deinterleaver(p(CacheBlockBytes)))
//   val yank    = LazyModule(new AXI4UserYanker)
//   val island  = LazyModule(new XilinxVC707MIGIsland(c, crossing))

//   val node: TLInwardNode =
//     island.crossAXI4In(island.node) := yank.node := deint.node := indexer.node := toaxi4.node := buffer.node

//   lazy val module = new LazyModuleImp(this) {
//     val io = IO(new Bundle {
//       val port = new XilinxVC707MIGIO(depth)
//     })

//     io.port <> island.module.io.port
//     dontTouch(io.port)
//   }
// }
