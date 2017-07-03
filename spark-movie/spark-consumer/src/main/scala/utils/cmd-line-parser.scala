package com.sparkmovie.utils

object CommandLineParser {

    def parseCmdLine(map : Map[String, String], args: List[String]) : Map[String, String] = {
        args match {
            case Nil => {
                map
            }
            case ("--adress" | "-a") :: value :: tail => {
                parseCmdLine(map ++ Map("adress" -> value), tail)
            }
            case ("--group-id" | "-gid") :: value :: tail => {
                parseCmdLine(map ++ Map("group-id" -> value), tail)
            }
            case string :: tail  => {
                parseCmdLine(map, tail)
            }
        }
    }

}
