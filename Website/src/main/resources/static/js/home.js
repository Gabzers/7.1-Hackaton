document.querySelectorAll('.genre-button').forEach(button => {
  button.addEventListener('click', () => {
    document.querySelectorAll('.genre-button').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');
    const selectedGenre = button.getAttribute('data-genre');
    console.log(`Selected genre: ${selectedGenre}`);
    // Add logic to filter movies based on the selected genre
  });
});