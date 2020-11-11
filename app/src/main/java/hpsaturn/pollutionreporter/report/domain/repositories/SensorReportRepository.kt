package hpsaturn.pollutionreporter.report.domain.repositories

import hpsaturn.pollutionreporter.report.domain.entities.SensorReportInformation

interface SensorReportRepository {
    suspend fun getPublicSensorReports(): List<SensorReportInformation>
}