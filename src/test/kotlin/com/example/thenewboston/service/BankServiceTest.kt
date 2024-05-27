package com.example.thenewboston.service

import com.example.thenewboston.datasource.BankDataSource
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import io.mockk.*

internal class BankServiceTest{

    private val dataSource: BankDataSource = mockk(relaxed = true)
    private val bankService = BankService(dataSource)
    @Test
    fun `should call its data source to retrieve banks`() {


    //when
    val banks = bankService.getBanks()


    //then

       verify(exactly = 1) { dataSource.retrieveBanks()}
    }
}   