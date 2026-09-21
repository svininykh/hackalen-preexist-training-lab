#!/usr/bin/env python3
"""Небольшой FAQ-бот без модели и без RAG."""

from __future__ import annotations

import re
from pathlib import Path


FAQ_PATH = Path(__file__).with_name("faq.txt")
TOKEN_RE = re.compile(r"[a-zA-Zа-яА-ЯёЁ0-9]+")
STOP_WORDS = {"а", "и", "в", "во", "на", "по", "с", "как", "какие", "какой", "у", "есть", "ли"}


def tokens(text: str) -> set[str]:
    """Возвращает значимые слова в нижнем регистре."""
    return {word.lower() for word in TOKEN_RE.findall(text) if word.lower() not in STOP_WORDS}


def load_faq(path: Path) -> list[tuple[str, str]]:
    entries: list[tuple[str, str]] = []
    question: str | None = None
    for line in path.read_text(encoding="utf-8").splitlines():
        if line.startswith("Вопрос: "):
            question = line.removeprefix("Вопрос: ")
        elif line.startswith("Ответ: ") and question:
            entries.append((question, line.removeprefix("Ответ: ")))
            question = None
    if len(entries) != 5:
        raise ValueError("В faq.txt должно быть ровно 5 пар «Вопрос» — «Ответ».")
    return entries


def find_answer(query: str, faq: list[tuple[str, str]]) -> str | None:
    query_tokens = tokens(query)
    if not query_tokens:
        return None

    best_score = 0.0
    best_answer: str | None = None
    for question, answer in faq:
        question_tokens = tokens(question)
        # Доля общих ключевых слов: 0 означает, что совпадения нет.
        score = len(query_tokens & question_tokens) / len(query_tokens | question_tokens)
        if score > best_score:
            best_score, best_answer = score, answer
    return best_answer


def main() -> None:
    faq = load_faq(FAQ_PATH)
    print("FAQ-бот. Задайте вопрос или введите «выход».")
    while True:
        try:
            query = input("> ").strip()
        except (EOFError, KeyboardInterrupt):
            print("\nДо свидания!")
            return
        if query.lower() in {"выход", "exit", "quit"}:
            print("До свидания!")
            return
        if not query:
            continue
        print(find_answer(query, faq) or "Не знаю.")


if __name__ == "__main__":
    main()
