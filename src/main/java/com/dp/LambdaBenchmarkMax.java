/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.dp;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class LambdaBenchmarkMax {

	private final static int MEASUREMENT_ITERATIONS = 5;
	private final static int WARMUP_ITERATIONS = 5;
	private final static int FORK_ITERATIONS = 2;
	private volatile int size = 1_000;
	private volatile List<Integer> ints = null;

	public static void main(String[] args) {
		LambdaBenchmarkMax benchmark = new LambdaBenchmarkMax();
		benchmark.setup();

		System.out.println("iteratorMaxInteger max is: " + benchmark.iteratorMaxInteger());
		System.out.println("forEachLoopMaxInteger max is: " + benchmark.forEachLoopMaxInteger());
		System.out.println("forEachLambdaMaxInteger max is: " + benchmark.forEachLambdaMaxInteger());
		System.out.println("forMaxInteger max is: " + benchmark.forMaxInteger());
		System.out.println("parallelStreamMaxInteger max is: " + benchmark.parallelStreamMaxInteger());
		System.out.println("streamReduceMaxInteger max is: " + benchmark.streamReduceMaxInteger());
		System.out.println("streamReduceMaxInteger max is: " + benchmark.streamMaxInteger());
		System.out.println("iteratorMaxInteger max is: " + benchmark.lambdaMaxInteger());
	}

	@Setup
	private void setup() {
		ints = new ArrayList<>(size);
		populate(ints);
	}

	private void populate(List<Integer> list) {
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			list.add(random.nextInt(size));
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int iteratorMaxInteger() {
		int max = Integer.MIN_VALUE;
		for (Iterator<Integer> it = ints.iterator(); it.hasNext(); ) {
			max = Integer.max(max, it.next());
		}
		return max;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int forEachLoopMaxInteger() {
		int max = Integer.MIN_VALUE;
		for (Integer n : ints) {
			max = Integer.max(max, n);
		}
		return max;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int forEachLambdaMaxInteger() {
		final Wrapper wrapper = new Wrapper();
		wrapper.inner = Integer.MIN_VALUE;

		ints.forEach(i -> updateMax(i, wrapper));
		return wrapper.inner;
	}

	private int updateMax(int i, Wrapper wrapper) {
		wrapper.inner = Math.max(i, wrapper.inner);
		return wrapper.inner;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int forMaxInteger() {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < size; i++) {
			max = Integer.max(max, ints.get(i));
		}
		return max;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int parallelStreamMaxInteger() {
		return ints.parallelStream().reduce(Integer::max).get();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int streamReduceMaxInteger() {
		return ints.stream().reduce(Integer::max).get();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int streamMaxInteger() {
		return ints.stream().max(Integer::compareTo).get();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(FORK_ITERATIONS)
	@Measurement(iterations = MEASUREMENT_ITERATIONS)
	@Warmup(iterations = WARMUP_ITERATIONS)
	private int lambdaMaxInteger() {
		return ints.stream().reduce(Integer.MIN_VALUE, Integer::max);
	}

	private static class Wrapper {
		private Integer inner;
	}

}