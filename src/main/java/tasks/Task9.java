package tasks;

import common.Person;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Objects;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  private long count;

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй

  // Комментарий:
  //    Чтобы не изменять входные данные и не использовать операцию remove(0) 
  // (которая может быть дорогой при использовании ArrayList), 
  // лучше использовать метод skip(1) для пропуска первого элемента потока. 
  // Это эффективнее и безопаснее.
  public List<String> getNames(List<Person> persons) {
    return persons.stream().skip(1).map(Person::firstName).toList();
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  
  // Комментарий:
  //   Убрала ненужное использование Stream, 
  // т.к. в данном случае достаточно создать HashSet из списка имен, 
  // который будет содержать только уникальные значения в силу своей структуры. 
  // Это упрощает код и улучшает его читаемость.
  public Set<String> getDifferentNames(List<Person> persons) {
    return new HashSet<>(getNames(persons));  
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО

  // Комментарий:
  //    Исправила двойную конкатенацию secondName 
  // на правильную последовательность: secondName, firstName, middleName.
  // А также заменила ручную конкатенацию на использование Stream с Collectors.joining.
  // Это делает код более чистым и читаемым.
  public String convertPersonToString(Person person) {
    return Stream.of(person.secondName(), person.firstName(), person.middleName())
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }

  // Словарь id персоны -> ее имя

  // Комментарий:
  //    Реализовала метод с помощью Collectors.toMap, который позволяет создать Map из потока.
  // Это более лаконично и эффективно, чем ручное заполнение Map.
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    return persons.stream()
      .collect(Collectors.toMap(
        Person::id,
        this::convertPersonToString,
        (existingValue, newValue) -> existingValue));
  }

  // Есть ли совпадающие в двух коллекциях персоны?

  // Комментарий:
  //    Вместо вложенного цикла для проверки наличия общих персон,
  // можно создать HashSet из второй коллекции и использовать метод anyMatch для проверки наличия совпадений. 
  // Это значительно улучшает производительность, особенно при больших коллекциях.
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    Set<Person> personsSet = new HashSet<>(persons2);
    return persons1.stream().anyMatch(personsSet::contains);
  }

  // Посчитать число четных чисел

  // Комментарий:
  //    Убрала ненужное использование forEach и ручного счетчика,
  // и заменила на использование метода count() после фильтрации четных чисел.
  // Также использовала битовую операцию для проверки четности, 
  // что может быть немного быстрее, чем использование оператора %.
  public long countEven(Stream<Integer> numbers) {
    return numbers.filter(num -> (num & 1) == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке

  // Комментарий:

  //    Это происходит из-за особенности hashCode() для Integer (хэш числа - это его значение),
  // созданию HashSet из списка, а не последовательного добавления с помощью add()
  // и благодаря тому, что максимальное значение элементов списка 10000 не превосходит размера списка.

  // Номер бакета, в который нужно положить элемент, 
  // определяется как остаток от деления хэша элемента на размер table (массив бакетов). 
  // Т.к. размер table будет 
  // n = 10`000 / 0.75 (округленное в большую сторону до ближайшей степени двойки), 
  // то для всех чисел от 1 до 10`000 хэш и будет являться номером бакета.
  // А т.к. хэш для Integer - это само число и числа все различны, 
  // то все элементы будут размещены в отдельных бакетах без коллизий.

  // Поэтому получается, что при создании HashSet число 1 попадает в бакет table[1], 
  // 2 - в table[2], 3 - в table[3], и т.д. 

  // А при вызове функции toString() преобразование как раз происходит по порядку от бакета table[0] до бакета table[n-1].
  // Поэтому и получится, что числа будут выведены в порядке возрастания.

  // Если же изменить функцию hashCode(), 
  // сделать последовательное добавление элементов 
  // или добавить значения чисел, превышающие размер списка, 
  // то порядок элементов будет отличаться от исходного, 
  // т.к. номер бакета уже не будет соответствовать значениям чисел
  // и могут возникнуть коллизии.
  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
  }
}
