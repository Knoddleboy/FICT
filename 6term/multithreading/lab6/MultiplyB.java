import java.util.Locale;

import mpi.*;

public class MultiplyB {
  private static final int MASTER = 0;
  private static final int FROM_MASTER = 1;
  private static final int FROM_WORKER = 2;

  public static void main(String[] args) {
    Matrix a = new Matrix(1000).randomize();
    Matrix b = new Matrix(1000).randomize();
    Matrix result = new Matrix(a.rows(), b.cols());

    MPI.Init(args);

    int currentProcess = MPI.COMM_WORLD.Rank();
    int nProcesses = MPI.COMM_WORLD.Size();
    int nWorkers = nProcesses - 1;

    if (nProcesses < 2) {
      MPI.COMM_WORLD.Abort(1);
      System.exit(1);
    }

    long startTime = System.currentTimeMillis();

    if (currentProcess == MASTER) {
      masterProcess(a, b, result, nWorkers);

      System.out
          .println(String.format(Locale.US, "[Blocking]\t|%dx%d * %dx%d|\twith %d workers:\n",
              a.rows(), a.cols(), b.rows(), b.cols(), nWorkers) +
              "Elapsed:\t " + (System.currentTimeMillis() - startTime) + " ms");
    } else {
      workerProcess(a.rows(), a.cols(), b.rows(), b.cols());
    }

    MPI.Finalize();
  }

  private static void masterProcess(Matrix a, Matrix b, Matrix result, int nWorkers) {
    int rowsPerWorker = a.rows() / nWorkers;
    int extraRows = a.rows() % nWorkers;

    for (int i = 1; i <= nWorkers; i++) {
      int startRow = (i - 1) * rowsPerWorker;
      int endRow = startRow + rowsPerWorker - 1;
      if (i == nWorkers) {
        endRow += extraRows;
      }

      Matrix subA = a.subMatrix(startRow, endRow, a.cols());
      double[] reducedSubA = subA.reduce();
      double[] reducedB = b.reduce();

      MPI.COMM_WORLD.Send(new int[] { startRow }, 0, 1, MPI.INT, i, FROM_MASTER);
      MPI.COMM_WORLD.Send(new int[] { endRow }, 0, 1, MPI.INT, i, FROM_MASTER);
      MPI.COMM_WORLD.Send(reducedSubA, 0, reducedSubA.length, MPI.DOUBLE, i, FROM_MASTER);
      MPI.COMM_WORLD.Send(reducedB, 0, reducedB.length, MPI.DOUBLE, i, FROM_MASTER);
    }

    for (int i = 1; i <= nWorkers; i++) {
      int[] startRow = new int[1];
      int[] endRow = new int[1];
      MPI.COMM_WORLD.Recv(startRow, 0, 1, MPI.INT, i, FROM_WORKER);
      MPI.COMM_WORLD.Recv(endRow, 0, 1, MPI.INT, i, FROM_WORKER);

      int offset = endRow[0] - startRow[0] + 1;
      int reducedResultSize = offset * result.cols() * Integer.BYTES;

      double[] reducedResult = new double[reducedResultSize];
      MPI.COMM_WORLD.Recv(reducedResult, 0, reducedResultSize, MPI.DOUBLE, i, FROM_WORKER);
      Matrix resultSubMatrix = new Matrix(reducedResult, offset, result.cols());

      result.replaceBlock(resultSubMatrix, startRow[0], endRow[0], result.cols());
    }
  }

  private static void workerProcess(int aRows, int aCols, int bRows, int bCols) {
    int[] startRow = new int[1];
    int[] endRow = new int[1];
    MPI.COMM_WORLD.Recv(startRow, 0, 1, MPI.INT, MASTER, FROM_MASTER);
    MPI.COMM_WORLD.Recv(endRow, 0, 1, MPI.INT, MASTER, FROM_MASTER);

    int offset = endRow[0] - startRow[0] + 1;
    int reducedSubASize = offset * aCols * Integer.BYTES;
    int reducedBSize = bRows * bCols * Integer.BYTES;

    double[] reducedSubA = new double[reducedSubASize];
    double[] reducedB = new double[reducedBSize];

    MPI.COMM_WORLD.Recv(reducedSubA, 0, reducedSubASize, MPI.DOUBLE, MASTER, FROM_MASTER);
    MPI.COMM_WORLD.Recv(reducedB, 0, reducedBSize, MPI.DOUBLE, MASTER, FROM_MASTER);

    Matrix subMatrixA = new Matrix(reducedSubA, offset, aCols);
    Matrix b = new Matrix(reducedB, bRows, bCols);

    double[] partialResult = subMatrixA.multiply(b).reduce();

    MPI.COMM_WORLD.Send(startRow, 0, 1, MPI.INT, MASTER, FROM_WORKER);
    MPI.COMM_WORLD.Send(endRow, 0, 1, MPI.INT, MASTER, FROM_WORKER);
    MPI.COMM_WORLD.Send(partialResult, 0, partialResult.length, MPI.DOUBLE, MASTER, FROM_WORKER);
  }
}
