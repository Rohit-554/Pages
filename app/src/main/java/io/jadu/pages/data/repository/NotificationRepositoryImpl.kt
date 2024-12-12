package io.jadu.pages.data.repository

import io.jadu.pages.domain.repository.NotificationRepository


class NotificationRepositoryImpl : NotificationRepository {

    private val messages = listOf(
        "Your genius idea deserves a place to shine—write it down before it escapes!",
        "The secret to productivity? One note at a time. Start jotting now!",
        "You’re just a few taps away from organized brilliance. Let’s get started!",
        "Note saved! It’s not just any note—it’s your masterpiece.",
        "Tweaked and polished! Your note is now as sharp as your ideas.",
        "Saved! Because even small thoughts can lead to big breakthroughs.",
        "Your note took a bow and exited the stage—deleted successfully!",
        "Goodbye, old note. Don’t worry, we’ll make room for something better!",
        "Clean slate alert! That note is history, just like you wanted.",
        "📸 Document scanned! It’s now sharper than your morning coffee.",
        "Scanned and saved—your papers are now part of the digital VIP club!",
        "Look at you, turning paper chaos into organized bliss!",
        "Psst... Your notes miss you! Come back and give them some love.",
        "Ideas don’t wait! Open the app and jot them down before they vanish.",
        "Your notes are like pizza toppings—customize them however you like!",
        "Another note? You’re on fire! (But don’t burn out.)",
        "Congratulations! You just gave your idea a digital passport.",
        "Your notes just got a glow-up. They’re basically celebrities now.",
        "Oops! That note is gone. But hey, you’re still awesome!",
        "Note app to brain: ‘Tag, you’re it!’ Time to share your ideas.",
        "If notes were gold, you’d be a billionaire by now. Keep going!",
        "You just saved a note. Somewhere, an idea fairy is doing a happy dance.",
        "Deleting notes like a ninja! Stealthy, but effective.",
        "Your app is now officially a vault of brilliance. Don’t lose the key!",
        "Note updated! It’s like a makeover, but for ideas.",
        "Your thoughts just went VIP. They’re now part of the elite note club!",
        "Who needs a memory palace when you’ve got this app? Write it down!",
        "Your ideas deserve a standing ovation. Keep the creativity coming!"
    )


    override fun getDailyNotificationContent(): String {
        return messages.random()
    }
}

