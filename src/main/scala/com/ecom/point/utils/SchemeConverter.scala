package com.ecom.point.utils

import zio.ZIO
import zio.prelude.{AssociativeFlatten, ForEach}

object SchemeConverter {
	implicit class SchemaF [F[+_]: ForEach, A ](data: F[A]) {
		def asModel[B](implicit f: A => B): F[B] = ForEach[F].map(f)(data)
	}
	
	implicit class SchemaFF[F[+_] : ForEach : AssociativeFlatten, A] (data: F[F[A]]) {
		def asModel[B](implicit f: A => B): F[B] = ForEach[F].map(f)(AssociativeFlatten[F].flatten(data))
	}
	
	implicit class ZioSchema [E, A](data: ZIO[Any, E, A]) {
		def asModel[B](implicit f: A => B): ZIO[Any, E, B] = data.map(f)
		def asModelWithMapError[E2 , B](fe: E => E2)(implicit f: A => B): ZIO[Any, E2, B] = data.mapBoth(fe, f)
	}
	
	implicit class ZioSchemaF [F[+_] : ForEach, E, A](data: ZIO[Any, E, F[A]]) {
		def asModel[B](implicit f: A => B): ZIO[Any, E, F[B]] = data.map(ex => ForEach[F].map(f)(ex))
		def asModelWithMapError[E2 , B](fe: E => E2)(implicit f: A => B): ZIO[Any, E2, F[B]] = data.mapBoth(fe, el => ForEach[F].map(f)(el))
	}
	
	implicit class ZioSchemaFF [F[+_] : ForEach : AssociativeFlatten, E, A](data: ZIO[Any, E, F[F[A]]]) {
		def asModel[B](implicit f: A => B): ZIO[Any, E, F[B]] = data.map(ex => ForEach[F].map(f)(AssociativeFlatten[F].flatten(ex)))
		def asModelWithMapError[E2 , B](fe: E => E2)(implicit f: A => B): ZIO[Any, E2, F[B]] = data.mapBoth(fe, el => ForEach[F].map(f)(AssociativeFlatten[F].flatten(el)))
	}
	
}
