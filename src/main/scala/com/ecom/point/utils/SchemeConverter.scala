package com.ecom.point.utils

import zio.ZIO
import zio.prelude.{AssociativeFlatten, ForEach}

object SchemeConverter {
	extension [F[+_]: ForEach, A ](data: F[A]) {
		def asModel[B](using f: A => B): F[B] = ForEach[F].map(f)(data)
	}
	
	extension[F[+_] : ForEach : AssociativeFlatten, A] (data: F[F[A]]) {
		def asModel[B](using f: A => B): F[B] = ForEach[F].map(f)(AssociativeFlatten[F].flatten(data))
	}
	
	extension [E, A](data: ZIO[Any, E, A]) {
		def asModel[B](using f: A => B): ZIO[Any, E, B] = data.map(f)
		def asModelWithMapError[E2 <: E, B](fe: E => E2)(using f: A => B): ZIO[Any, E2, B] = data.mapBoth(fe, f)
	}
	
	extension [F[+_] : ForEach, E, A](data: ZIO[Any, E, F[A]]) {
		def asModel[B](using f: A => B): ZIO[Any, E, F[B]] = data.map(ex => ForEach[F].map(f)(ex))
		def asModelWithMapError[E2 <: E, B](fe: E => E2)(using f: A => B): ZIO[Any, E2, F[B]] = data.mapBoth(fe, el => ForEach[F].map(f)(el))
	}
	
	extension [F[+_] : ForEach : AssociativeFlatten, E, A](data: ZIO[Any, E, F[F[A]]]) {
		def asModel[B](using f: A => B): ZIO[Any, E, F[B]] = data.map(ex => ForEach[F].map(f)(AssociativeFlatten[F].flatten(ex)))
		def asModelWithMapError[E2 <: E, B](fe: E => E2)(using f: A => B): ZIO[Any, E2, F[B]] = data.mapBoth(fe, el => ForEach[F].map(f)(AssociativeFlatten[F].flatten(el)))
	}
	
}
