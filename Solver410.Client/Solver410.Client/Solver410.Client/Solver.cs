using System;
using System.Collections.Generic;
using System.Linq;

namespace Solver410.Client
{
    public class Solver
    {
        private const double TargetValue = 10.0;
        private const double Epsilon = 0.0001;

        public enum Operator { Add, Sub, Mul, Div }

        public static List<string> Solve(double n1, double n2, double n3, double n4,
                                 bool useAdd, bool useSub, bool useMul, bool useDiv)
        {
            var solutions = new HashSet<string>();
            var numbers = new[] { n1, n2, n3, n4 };
            var permutations = GetPermutations(numbers);

            // CRIA A LISTA DINAMICAMENTE BASEADA NOS BOOLEANOS
            var opList = new List<Operator>();
            if (useAdd) opList.Add(Operator.Add);
            if (useSub) opList.Add(Operator.Sub);
            if (useMul) opList.Add(Operator.Mul);
            if (useDiv) opList.Add(Operator.Div);

            // Se o usuário não marcar nada, retornamos vazio para não dar erro
            if (opList.Count == 0) return new List<string>();

            var operators = opList.ToArray();

            foreach (var p in permutations)
            {
                foreach (var op1 in operators)
                {
                    foreach (var op2 in operators)
                    {
                        foreach (var op3 in operators)
                        {
                            TryAddSolution(solutions, p, op1, op2, op3);
                        }
                    }
                }
            }

            return solutions.ToList();
        }

        private static void TryAddSolution(HashSet<string> solutions, double[] p, Operator op1, Operator op2, Operator op3)
        {
            double a = p[0], b = p[1], c = p[2], d = p[3];

            // Pattern 1: ((A op B) op C) op D
            double r1 = Calculate(a, b, op1);
            double r2 = Calculate(r1, c, op2);
            double res1 = Calculate(r2, d, op3);

            if (IsTarget(res1))
            {
                var s = FormatLeftAssociative(a, b, c, d, op1, op2, op3);
                if (s != null) solutions.Add(s);
            }

            // Pattern 2: (A op B) op (C op D)
            double r3 = Calculate(c, d, op3);
            double res2 = Calculate(r1, r3, op2);

            if (IsTarget(res2))
            {
                var s = FormatDisjoint(a, b, c, d, op1, op2, op3);
                if (s != null) solutions.Add(s);
            }

            // Pattern 3: A op (B op C) op D (Middle priority)
            double rMid = Calculate(b, c, op2);
            double rLeft = Calculate(a, rMid, op1);
            double res3 = Calculate(rLeft, d, op3);

            if (IsTarget(res3))
            {
                var s = FormatMiddle(a, b, c, d, op1, op2, op3);
                if (s != null) solutions.Add(s);
            }
        }

        private static double Calculate(double a, double b, Operator op)
        {
            return op switch
            {
                Operator.Add => a + b,
                Operator.Sub => a - b,
                Operator.Mul => a * b,
                Operator.Div => Math.Abs(b) < Epsilon ? double.NaN : a / b,
                _ => double.NaN
            };
        }

        private static bool IsTarget(double value) =>
            !double.IsNaN(value) && Math.Abs(value - TargetValue) < Epsilon;

        private static string GetSymbol(Operator op) => op switch
        {
            Operator.Add => "+",
            Operator.Sub => "-",
            Operator.Mul => "*",
            Operator.Div => "/",
            _ => "?"
        };

        private static bool RequiresParentheses(Operator inner, Operator outer, bool isRightSide)
        {
            bool innerWeak = inner == Operator.Add || inner == Operator.Sub;
            bool outerStrong = outer == Operator.Mul || outer == Operator.Div;

            if (innerWeak && outerStrong) return true;

            if (isRightSide)
            {
                if (outer == Operator.Sub && innerWeak) return true;
                if (outer == Operator.Div && (inner == Operator.Mul || inner == Operator.Div)) return true;
            }

            return false;
        }

        private static string FormatLeftAssociative(double a, double b, double c, double d, Operator o1, Operator o2, Operator o3)
        {
            if (RequiresParentheses(o1, o2, false) && RequiresParentheses(o2, o3, false)) return null;

            string p1 = FormatSimple(a, b, o1);
            if (RequiresParentheses(o1, o2, false)) p1 = $"({p1})";

            string p2 = $"{p1} {GetSymbol(o2)} {(int)c}";
            if (RequiresParentheses(o2, o3, false)) p2 = $"({p2})";

            return $"{p2} {GetSymbol(o3)} {(int)d}";
        }

        private static string FormatDisjoint(double a, double b, double c, double d, Operator o1, Operator o2, Operator o3)
        {
            if (RequiresParentheses(o1, o2, false) && RequiresParentheses(o3, o2, true)) return null;

            string left = FormatSimple(a, b, o1);
            if (RequiresParentheses(o1, o2, false)) left = $"({left})";

            string right = FormatSimple(c, d, o3);
            if (RequiresParentheses(o3, o2, true)) right = $"({right})";

            return $"{left} {GetSymbol(o2)} {right}";
        }

        private static string FormatMiddle(double a, double b, double c, double d, Operator o1, Operator o2, Operator o3)
        {
            bool pMid = RequiresParentheses(o2, o1, true) || RequiresParentheses(o2, o3, false);
            bool pGlobal = RequiresParentheses(o1, o3, false);

            if (pMid && pGlobal) return null;

            string mid = FormatSimple(b, c, o2);
            if (pMid) mid = $"({mid})";

            string leftPart = $"{(int)a} {GetSymbol(o1)} {mid}";
            if (pGlobal) leftPart = $"({leftPart})";

            return $"{leftPart} {GetSymbol(o3)} {(int)d}";
        }

        private static string FormatSimple(double a, double b, Operator op) =>
            $"{(int)a} {GetSymbol(op)} {(int)b}";

        private static List<double[]> GetPermutations(double[] list)
        {
            var results = new List<double[]>();
            Permute(list, 0, list.Length - 1, results);
            return results;
        }

        private static void Permute(double[] list, int start, int end, List<double[]> results)
        {
            if (start == end)
            {
                if (!results.Any(x => x.SequenceEqual(list)))
                {
                    results.Add((double[])list.Clone());
                }
            }
            else
            {
                for (int i = start; i <= end; i++)
                {
                    Swap(ref list[start], ref list[i]);
                    Permute(list, start + 1, end, results);
                    Swap(ref list[start], ref list[i]);
                }
            }
        }

        private static void Swap(ref double a, ref double b)
        {
            (a, b) = (b, a);
        }
    }
}