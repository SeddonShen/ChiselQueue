import chisel3._
import chisel3.util._

class StoreOrLoadInfo extends Bundle {
  val addr = UInt(32.W)
  val data = UInt(64.W)
}
class QueueModule extends Module{
  val io = IO (new Bundle{
    // val in  = Flipped(Decoupled(UInt(8.W)))
    val in  = Flipped(Decoupled(new StoreOrLoadInfo()))
    val out = Decoupled(new StoreOrLoadInfo())
  })

  val queue = Queue(io.in, 2)  // 2-element queue
  io.out <> queue
}