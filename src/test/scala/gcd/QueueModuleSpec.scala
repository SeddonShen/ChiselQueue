// See README.md for license details.



import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

class QueueModuleSpec extends AnyFreeSpec with ChiselScalatestTester {

  "QueueModule should pass" in {
    test(new QueueModule()) { c =>
      c.io.out.ready.poke(false.B)	// 不能输出值
      c.io.in.valid.poke(true.B)  // 可以入列
      c.io.in.bits.poke((new StoreOrLoadInfo).Lit(_.addr -> 1.U, _.data -> 2.U))
      println(s"Starting:")
      println(s"\tio.in: ready=${c.io.in.ready.peek()}")
      println(s"\tio.out: valid=${c.io.out.valid.peek()}, bits=${c.io.out.bits.peek()}")
      c.clock.step(1)	// 42入列

      c.io.in.valid.poke(true.B)  // 另一个元素可以入列
      c.io.in.bits.poke((new StoreOrLoadInfo).Lit(_.addr -> 3.U, _.data -> 4.U))	
      // 分析看看io.out.valid和io.out.bits会输出什么
      println(s"After first enqueue:")
      println(s"\tio.in: ready=${c.io.in.ready.peek()}")
      println(s"\tio.out: valid=${c.io.out.valid.peek()}, bits=${c.io.out.bits.peek()}")
      c.clock.step(1)	// (3,4)入列

      c.io.in.valid.poke(true.B)  // 准备入列，但是队列是满的
      c.io.in.bits.poke((new StoreOrLoadInfo).Lit(_.addr -> 5.U, _.data -> 6.U))	// 进不去了
      c.io.out.ready.poke(true.B)	// 可以出列了
      // What do you think io.in.ready will be, and will this enqueue succeed, and what will be read?
      println(s"On first read:")
      println(s"\tio.in: ready=${c.io.in.ready.peek()}")
      println(s"\tio.out: valid=${c.io.out.valid.peek()}, bits=${c.io.out.bits.peek()}")
      c.clock.step(1)	// 第一次(1,2)出列，(5,6)入列失败

      // c.io.in.valid.poke(false.B)  // 不读元素了
      // c.io.out.ready.poke(true.B)	// 可以继续输出
      // // What do you think will be read here?
      // println(s"On second read:")
      // println(s"\tio.in: ready=${c.io.in.ready.peek()}")
      // println(s"\tio.out: valid=${c.io.out.valid.peek()}, bits=${c.io.out.bits.peek()}")
      // c.clock.step(1)	// 43出列，队列空了

      // // 这时候还会读到东西吗？
      // println(s"On third read:")
      // println(s"\tio.in: ready=${c.io.in.ready.peek()}")
      // println(s"\tio.out: valid=${c.io.out.valid.peek()}, bits=${c.io.out.bits.peek()}")
      // c.clock.step(1)
    }
  }
}
