package models.ui

sealed trait Errors
final case class ResourceNotFound(message: String) extends Errors
