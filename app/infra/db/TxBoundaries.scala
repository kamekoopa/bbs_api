package infra.db

import scalikejdbc.{Tx, TxBoundary}

import scalaz.{\/-, -\/, \/}

object TxBoundaries {

  implicit def v[L, R] = new TxBoundary[L\/R] {
    def finishTx(result: \/[L, R], tx: Tx): \/[L, R] = result match {
      case -\/(_) => tx.rollback(); result
      case \/-(_) => tx.commit(); result
    }
  }
}
