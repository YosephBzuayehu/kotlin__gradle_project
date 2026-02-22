package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "7" -> {
            listAllPatrons(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

fun addBook(service: LibraryService) {
    println("\n--- Add New Book ---")
    print("ISBN: ")
    val isbn = readln()
    print("Title: ")
    val title = readln()
    print("Author: ")
    val author = readln()
    print("Year: ")
    val year = readln().toIntOrNull() ?: 0

    val book = Book(isbn, title, author, year)
    if (service.addBook(book)) {
        println("Book added successfully!")
    } else {
        println("Failed to add book. ISBN already exists.")
    }
}

fun registerPatron(repository: LibraryRepository) {
    println("\n--- Register New Patron ---")
    print("Patron ID: ")
    val id = readln()
    print("Name: ")
    val name = readln()
    print("Email: ")
    val email = readln()
    print("Phone: ")
    val phone = readln()

    val patron = Patron(id, name, email, phone)
    if (repository.addPatron(patron)) {
        println("Patron registered successfully!")
    } else {
        println("Failed to register patron. ID already exists.")
    }
}

fun borrowBook(service: LibraryService) {
    println("\n--- Borrow Book ---")
    print("Patron ID: ")
    val patronId = readln()
    print("Book ISBN: ")
    val isbn = readln()

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Book borrowed successfully!")
        BorrowResult.BOOK_NOT_FOUND -> println("Error: Book not found.")
        BorrowResult.PATRON_NOT_FOUND -> println("Error: Patron not found.")
        BorrowResult.NOT_AVAILABLE -> println("Error: Book is already borrowed.")
        BorrowResult.LIMIT_REACHED -> println("Error: Patron has reached the borrowing limit.")
    }
}

fun returnBook(service: LibraryService) {
    println("\n--- Return Book ---")
    print("Patron ID: ")
    val patronId = readln()
    print("Book ISBN: ")
    val isbn = readln()

    if (service.returnBook(patronId, isbn)) {
        println("Book returned successfully!")
    } else {
        println("Failed to return book. Check if the IDs are correct and the book was borrowed by this patron.")
    }
}

fun search(service: LibraryService) {
    println("\n--- Search Books ---")
    print("Enter search query (title or author): ")
    val query = readln()
    val results = service.search(query)

    if (results.isEmpty()) {
        println("No books found.")
    } else {
        println("\n--- Search Results ---")
        results.forEach { println("${it.isbn} | ${it.title} by ${it.author} (${it.year}) - ${if (it.isAvailable) "Available" else "Borrowed"}") }
    }
}

fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    println("\n--- All Books ---")
    if (books.isEmpty()) {
        println("Library is empty.")
    } else {
        books.forEach { println("${it.isbn} | ${it.title} by ${it.author} (${it.year}) - ${if (it.isAvailable) "Available" else "Borrowed"}") }
    }
}

fun listAllPatrons(repository: LibraryRepository) {
    val patrons = repository.getAllPatrons()
    println("\n--- All Patrons ---")
    if (patrons.isEmpty()) {
        println("No patrons registered.")
    } else {
        patrons.forEach { patron ->
            println("${patron.id} | ${patron.name} (${patron.email}, ${patron.phone})")
            if (patron.borrowedBooks.isNotEmpty()) {
                println("  Borrowed: ${patron.borrowedBooks.joinToString(", ")}")
            }
        }
    }
}